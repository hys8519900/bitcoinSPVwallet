package listeners;

import com.google.bitcoin.core.AbstractPeerEventListener;
import com.google.bitcoin.core.Peer;

import foo.UItest;



public class NetPeerListener extends AbstractPeerEventListener
{
	@Override
	public void onPeerConnected(Peer peer, int peerCount)
	{
		UItest.UpdateNetLabel(peerCount);
	}
	
	@Override
	public void onPeerDisconnected(Peer peer, int peerCount)
	{
		UItest.UpdateNetLabel(peerCount);
	}
}
