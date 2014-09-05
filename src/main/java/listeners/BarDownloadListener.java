package listeners;

import com.google.bitcoin.core.DownloadListener;

import java.util.Date;

import foo.UItest;
import foo.UItest.*;

public class BarDownloadListener extends DownloadListener
{
	@Override
	protected void progress(double pct, int blocksSoFar, Date date) {
		UItest.UpdateDownloadBarValue((int)pct);
		UItest.UpdateDownloadBarText(((int)pct) + "%");
		if((int)pct == 100)
		{
			UItest.UpdateDownloadBarText("同步完成");
		}
    }
	
	@Override
	protected void doneDownload()
	{
		UItest.UpdateDownloadBarText("同步完成");
	}
	
	@Override
	protected void startDownload(int blocks)
	{
		UItest.UpdateDownloadBarText("正在同步");
	}
}

