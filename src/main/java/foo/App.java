package foo;

import com.google.bitcoin.core.*;
import com.google.bitcoin.crypto.KeyCrypterException;
import com.google.bitcoin.kits.WalletAppKit;
import com.google.bitcoin.params.MainNetParams;
import com.google.bitcoin.params.RegTestParams;
import com.google.bitcoin.params.TestNet3Params;
import com.google.bitcoin.utils.BriefLogFormatter;
import com.google.bitcoin.utils.Threading;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;

import java.io.File;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Executor;

import javax.swing.SwingUtilities;

import static com.google.common.base.Preconditions.checkNotNull;
import listeners.BarDownloadListener;
import listeners.NetPeerListener;

public class App 
{
	private static Address forwardingAddress;
    private static WalletAppKit kit;
    private static NetworkParameters params;

    private static Address sendToAddress;
    
    //save table row and txhash
    private static Vector<Sha256Hash> txhash;
    
    public static void main(String[] args) throws Exception {
        // This line makes the log output more compact and easily read, especially when using the JDK log adapter.
        BriefLogFormatter.init();

        // Figure out which network we should connect to. Each one gets its own set of files.
       
        String filePrefix;
        
        params = TestNet3Params.get();
        filePrefix = "forwarding-service-testnet";
    
        // Parse the address given as the first parameter.
        forwardingAddress = new Address(params, "mueYz6zP6r5RADoqwAhasVkj4fUoiKBcuw");

        // Start up a basic app using a class that automates some boilerplate.
        kit = new WalletAppKit(params, new File("."), filePrefix)
        {
        	@Override
        	public void onSetupCompleted()
        	{
        		kit.setBlockingStartup(false);
        		kit.setDownloadListener(new BarDownloadListener());
        		kit.peerGroup().addEventListener(new NetPeerListener());
        	}
        };

        if (params == RegTestParams.get()) {
            // Regression test mode is designed for testing and development only, so there's no public network for it.
            // If you pick this mode, you're expected to be running a local "bitcoind -regtest" instance.
            kit.connectToLocalHost();
        }

      //UI
        //run a new executor and change the USER_THREAD
        Executor runInUIThread = new Executor()
        {
        	@Override
        	public void execute(Runnable runnable)
        	{
        		SwingUtilities.invokeLater(runnable);
        	}
        };
        
        Runnable UIrunable = new Runnable()
        {
        	public void run()
        	{
        		//UItest myUItest = new UItest();
        		UItest.createAndShowGUI();
        		//update balance , convert nanocoin to BTC
        		UItest.UpdateBalance(Utils.bitcoinValueToFriendlyString((kit.wallet().getBalance())));
        		UItest.UpdateWalletAddress(getMyAddressString());
        	}
        };
        runInUIThread.execute(UIrunable);
        Threading.USER_THREAD = runInUIThread;
        
        // Download the block chain and wait until it's done.
        kit.startAndWait();

        // We want to know when we receive money.
        kit.wallet().addEventListener(new AbstractWalletEventListener() {
            @Override
            public void onCoinsReceived(Wallet w, Transaction tx, BigInteger prevBalance, BigInteger newBalance) {
                //displayTrayMessage
            	if(newBalance.compareTo(prevBalance) == 1)
            	{
            		UItest.displayTrayMessage("Received tx", Utils.bitcoinValueToFriendlyString(tx.getValueSentToMe(w)) +
            				"BTC" + "\nTransaction will be forwarded after it confirms.");
            	}

            	// Runs in the dedicated "user thread" (see bitcoinj docs for more info on this).
                //
                // The transaction "tx" can either be pending, or included into a block (we didn't see the broadcast).
                BigInteger value = tx.getValueSentToMe(w);
                System.out.println("Received tx for " + Utils.bitcoinValueToFriendlyString(value) + ": " + tx);
                System.out.println("Transaction will be forwarded after it confirms.");
                
                
                // Wait until it's made it into the block chain (may run immediately if it's already there).
                //
                // For this dummy app of course, we could just forward the unconfirmed transaction. If it were
                // to be double spent, no harm done. Wallet.allowSpendingUnconfirmedTransactions() would have to
                // be called in onSetupCompleted() above. But we don't do that here to demonstrate the more common
                // case of waiting for a block.
                Futures.addCallback(tx.getConfidence().getDepthFuture(1), new FutureCallback<Transaction>() {
                    @Override
                    public void onSuccess(Transaction result) {
                        // "result" here is the same as "tx" above, but we use it anyway for clarity.
                        //forwardCoins(result);
                    	System.out.println("\ntx: " + result.toString() + "\nget depth 1.");
                    	
                    	UItest.displayTrayMessage("tx confidence get depth 1", result.getHashAsString());
                    	 //////////////////////////////////
                        //Update UI
                        UItest.UpdateBalance(Utils.bitcoinValueToFriendlyString((kit.wallet().getBalance())));    
                        ///////
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        // This kind of future can't fail, just rethrow in case something weird happens.
                        throw new RuntimeException(t);
                    }
                });
            }
            
            @Override
            public void onCoinsSent(Wallet w, Transaction tx, BigInteger prevBalance, BigInteger newBalance)
            {
            	BigInteger valueSend = tx.getValueSentFromMe(w).subtract(tx.getValueSentToMe(w));
            	System.out.println("Send coins: " + Utils.bitcoinValueToFriendlyString(valueSend));
            	if(prevBalance.compareTo(newBalance) == 1)
            	{
            		UItest.displayTrayMessage("Send tx", Utils.bitcoinValueToFriendlyString(valueSend) +
            				"BTC" + "\nTransaction will be forwarded after it confirms.");
            	}
            	
            	Futures.addCallback(tx.getConfidence().getDepthFuture(1), new FutureCallback<Transaction>()
            			{
            				@Override
            				public void onSuccess(Transaction result)
            				{
            					UItest.displayTrayMessage("tx confidence get depth 1", result.getHashAsString());
            				}
            				@Override
            				public void onFailure(Throwable t)
            				{
            					throw new RuntimeException(t);
            				}
            			});
            }
        });

        sendToAddress = kit.wallet().getKeys().get(0).toAddress(params);
        System.out.println("Send coins to: " + sendToAddress);
        System.out.println("Waiting for coins to arrive. Press Ctrl-C to quit.");
        //System.out.println(kit.wallet().toString());
        
        
        //get all tx
        /*
        Set<Transaction> txSet = kit.wallet().getTransactions(true);
        Iterator<Transaction> txIter = txSet.iterator();
        System.out.println(txSet.size());
        while(txIter.hasNext())
        {
        	System.out.println(txIter.next().toString() + "\n\n");
        }
        */
        
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException ignored) {}
    }

    //print wallet on the console
    public static void printtest()
    {
    	System.out.println(kit.wallet().toString());
    }
    
    //tx --> one row data
    public static Vector<String> formatTxtoTable(Transaction tx)
    {
    	Vector<String> vt = new Vector<String>(4);
    	
    	if(tx.isPending())
    	{
    		vt.add("pending");
    	}
    	else
    	{
    		vt.add("mature");
    	}
    	
    	vt.add(tx.getUpdateTime().toString());
    	
    	// >0 is receive <0 is send
    	//receive show txin address send show txout
    	if(tx.getValue(kit.wallet()).intValue() < 0)
    	{
    		//TODO get out address
    		if(kit.wallet().getKeys().get(0).toAddress(params).toString() != tx.getOutput(0).getScriptPubKey().getToAddress(params).toString())
    		{
    			vt.add("Send to: " + tx.getOutput(0).getScriptPubKey().getToAddress(params).toString());
    		}
    		else
    		{
    			vt.add("Send to: " + tx.getOutput(1).getScriptPubKey().getToAddress(params).toString());
    		}
    	}
    	else
    	{
    		vt.add("Receive from: " + tx.getInput(0).getScriptSig().getFromAddress(params).toString());
    	}
    	
    	//add amount
    	vt.add(Utils.bitcoinValueToFriendlyString(tx.getValue(kit.wallet())));
    	
    	return vt;
    }
    
    ///////////////////////
    //Table
    public static Vector<Vector> getTableDataFromWallet()
    {
    	Set<Transaction> txSet = kit.wallet().getTransactions(false);
        Iterator<Transaction> txIter = txSet.iterator();
       
        Vector<Vector> rowData = new Vector<Vector>(1);
        txhash = new Vector<Sha256Hash>();
        txhash.clear();
        
        while(txIter.hasNext())
        {
        	Transaction tx = txIter.next();
        	rowData.add(formatTxtoTable(tx));
        	//add tx hash with row number
        	txhash.add(tx.getHash());
        }
        
        return rowData;
    }
    
    public static Sha256Hash getTxhashFromRow(int index)
    {
    	return txhash.get(index);
    }
    
    public static Transaction getTransactionFormTxhash(Sha256Hash txhash)
    {
    	return kit.wallet().getTransaction(txhash);
    }
    
    ////////////////////////////////////////
    public static String getMyAddressString()
    {
    	return kit.wallet().getKeys().get(0).toAddress(params).toString();
    }
    
    ///////////////////////////Static UI method
    //simple send coins without check broadcast success
    public static void simpleSend(String coins)
    {
    	try {
			final Wallet.SendResult sendResult =  kit.wallet().sendCoins(kit.peerGroup(), forwardingAddress, Utils.toNanoCoins(coins));
			checkNotNull(sendResult);
    	} catch (InsufficientMoneyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void simpleSendToAddress(String coins, String address)
    {
    	try {
    		Address sendaddress = new Address(params, address);
			final Wallet.SendResult sendResult =  kit.wallet().sendCoins(kit.peerGroup(), sendaddress, Utils.toNanoCoins(coins));
			checkNotNull(sendResult);
    	} catch (InsufficientMoneyException | AddressFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    //for testButton
    public static void testAction()
    {
    	System.out.println(kit.peerGroup().getConnectedPeers().size());
    	System.out.println(kit.peerGroup().getConnectedPeers());
    }
    
    ////////////////////////////////////////////////////////
    
    //send coins by tx, check the broadcast
    private static void forwardCoins(Transaction tx) {
        try {
            BigInteger value = tx.getValueSentToMe(kit.wallet());
            System.out.println("Forwarding " + Utils.bitcoinValueToFriendlyString(value) + " BTC");
            // Now send the coins back! Send with a small fee attached to ensure rapid confirmation.
            final BigInteger amountToSend = value.subtract(Transaction.REFERENCE_DEFAULT_MIN_TX_FEE);
            final Wallet.SendResult sendResult = kit.wallet().sendCoins(kit.peerGroup(), forwardingAddress, amountToSend);
            checkNotNull(sendResult);  // We should never try to send more coins than we have!
            System.out.println("Sending ...");
            // Register a callback that is invoked when the transaction has propagated across the network.
            // This shows a second style of registering ListenableFuture callbacks, it works when you don't
            // need access to the object the future returns.
            sendResult.broadcastComplete.addListener(new Runnable() {
                @Override
                public void run() {
                    // The wallet has changed now, it'll get auto saved shortly or when the app shuts down.
                    System.out.println("Sent coins onwards! Transaction hash is " + sendResult.tx.getHashAsString());
                }
            }, MoreExecutors.sameThreadExecutor());
        } catch (KeyCrypterException e) {
            // We don't use encrypted wallets in this example - can never happen.
            throw new RuntimeException(e);
        } catch (InsufficientMoneyException e) {
            // This should never happen - we're only trying to forward what we received!
            throw new RuntimeException(e);
        }
    }
}
