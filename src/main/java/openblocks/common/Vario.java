package openblocks.common;

public class Vario {

	private static final int WATCHDOG_PERIOD = 1000 / 20; // nominal game tick

	private static final int WATCHDOG_TIMEOUT_TICKS = 10;

	private static final int VOL_MIN = 2;
	private static final int VOL_MAX = 20;

	public static final Vario instance = new Vario();

	private int varioVolume = 8;

	private boolean isAlive;

	private boolean isEnabled;

	private IVarioController activeController = IVarioController.NULL;

	private int watchdogMissedTicks;

	private Thread watchdogThread;

	private final BeepGenerator beeper = new BeepGenerator();

	{
		setVolume(varioVolume);
	}

	private class Controller implements IVarioController {

		private boolean isValid = true;

		@Override
		public void setFrequencies(double toneFrequency, double beepFrequency) {
			if (isValid) {
				beeper.setTargetToneFrequency(toneFrequency);
				beeper.setBeepFrequency(beepFrequency);
			}
		}

		@Override
		public void keepAlive() {
			if (isValid)
				resetWatchdog();
		}

		@Override
		public void kill() {
			if (isValid)
				isAlive = false;
		}

		@Override
		public boolean isValid() {
			return isValid;
		}

		@Override
		public void release() {
			isValid = false;
		}
	}

	public void incVolume() {
		varioVolume = Math.min((varioVolume + 2), VOL_MAX);
		setVolume(varioVolume);
	}

	public void decVolume() {
		varioVolume = Math.max((varioVolume - 2), VOL_MIN);
		setVolume(varioVolume);
	}

	private void setVolume(int volume) {
		beeper.setVolume((short)(varioVolume << 8));
	}

	public void enable() {
		isEnabled = true;
	}

	public void disable() {
		isEnabled = false;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public void toggle() {
		isEnabled = !isEnabled;
	}

	public IVarioController acquire() {
		if (activeController.isValid())
			activeController.release();

		return (activeController = new Controller());
	}

	private void resetWatchdog() {
		if (isEnabled) {
			watchdogMissedTicks = 0;

			if (watchdogThread == null || !watchdogThread.isAlive()) {
				watchdogThread = startWatchdog();
			}
		}
	}

	private Thread startWatchdog() {
		isAlive = true;

		final Thread watchdogThread = new Thread(new Runnable() {
			@Override
			public void run() {
				beeper.start();

				try {
					while (isAlive && isEnabled) {
						if (watchdogMissedTicks++ > WATCHDOG_TIMEOUT_TICKS)
							break;

						try {
							Thread.sleep(WATCHDOG_PERIOD);
						} catch (InterruptedException e) {
							break;
						}
					}
				} finally {
					beeper.stop();
					isAlive = false;
				}
			}
		});

		watchdogThread.setName("Vario watchdog");
		watchdogThread.setDaemon(true);
		watchdogThread.start();

		return watchdogThread;
	}
}