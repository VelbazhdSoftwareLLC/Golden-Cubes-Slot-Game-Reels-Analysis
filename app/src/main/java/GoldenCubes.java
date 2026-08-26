import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class GoldenCubes {
    public static final double RTP_TARGET = 0.96;

	public class Model {
		private static final int[][] PRIZES = {
			{ 0,   0,  0,  0, 0, 0, 0, 0,},
			{ 0,   0,  0,  0, 0, 0, 0, 0,},
			{ 1,   0,  0,  0, 0, 0, 0, 1,},
			{ 2,  20, 10, 10, 4, 4, 4, 4,},
			{10, 200, 40, 40,10,10,10,10,},
			{50,1000,100,100,40,40,40,40,},
		};

        public static final String[] SYMBOLS = {"star", "roku", "one", "two", "three", "four", "five", "six"};

		public List<List<String>> baseReels = new ArrayList<>();

        public Map<String, List<Integer>> payTable = new HashMap<>();

		private static final int[][] LINES = {
			{1,1,1,1,1,},
			{0,0,0,0,0,},
			{2,2,2,2,2,},
			{0,1,2,1,0,},
			{2,1,0,1,2,},
		};

        public Model() {
            for(int i=0; i<5; i++) {
                baseReels.add(new ArrayList<>());
            }

            for (int i = 0; i < SYMBOLS.length; i++) {
                List<Integer> payouts = new ArrayList<>();
                for (int count = 0; count < PRIZES.length; count++) {
                    payouts.add(PRIZES[count][i]);
                }
                payTable.put(SYMBOLS[i], payouts);
            }
        }
	}

   	public class Statistics {
		public long wonMoney = 0L;
		public long lostMoney = 0L;
		public long baseMoney = 0L;
		public long baseHitFrequency = 0L;
		public long totalNumberOfGames = 0L;
		public long numberOfBaseGameSpins = 1_000_000L;
		Map<Integer, Long> winHistogram = new HashMap<>();
	}

	private static final Random PRNG = ThreadLocalRandom.current();

    public Model model = new Model();
	public Statistics statistics = new Statistics();

	private void spin(List<List<String>> reels, String[][] view) {
		for (int i = 0; i < view.length && i < reels.size(); i++) {
			int r = PRNG.nextInt(reels.get(i).size());
			int u = r - 1;
			int d = r + 1;

			if (u < 0) {
				u = reels.get(i).size() - 1;
			}

			if (d >= reels.get(i).size()) {
				d = 0;
			}

			view[i][0] = reels.get(i).get(u);
			view[i][1] = reels.get(i).get(r);
			view[i][2] = reels.get(i).get(d);
		}
    }

   	private int scatterWin(String[][] view) {
		int count = 0;
		for (int j = 0; j < 3; j++) {
			for (int i = 0; i < 5; i++) {
				if (view[i][j].equals("star")) {
					count++;
				}
			}
		}

        return model.payTable.get("star").get(count);
    }

   	private int lineWin(String[] line) {
		String symbol = line[0];

        /* Scatter does not form line win. */
        if(symbol.equals("star")) {
            return 0;
        }

		int count = 0;
		for (int i = 0; i < line.length; i++) {
			if (line[i].equals(symbol)) {
				count++;
			} else {
				break;
			}
		}
		
		return model.payTable.get(symbol).get(count);
	}

    private int linesWin(String[][] view) {
		int win = 0;
		String[] line = { null, null, null, null, null };
		for (int l = 0; l < model.LINES.length; l++) {
			for (int i = 0; i < line.length; i++) {
				int index = model.LINES[l][i];
				line[i] = view[i][index];
			}

			win += lineWin(line);
		}
		return win;
	}

	private void singleBaseGame(String[][] view) {
		spin(model.baseReels, view);

		int win = scatterWin(view) + linesWin(view);
		if (win > 0) {
			statistics.baseHitFrequency++;
			statistics.baseMoney += win;
			statistics.wonMoney += win;
		}

		statistics.winHistogram.merge(win, 1L, Long::sum);
	}

    public void simulate() {
		String[][] view = {
			{ null, null, null },
			{ null, null, null },
			{ null, null, null },
			{ null, null, null },
			{ null, null, null },
		};

		for (long g = 0L; g < statistics.numberOfBaseGameSpins; g++) {
			statistics.totalNumberOfGames++;
			statistics.lostMoney += model.LINES.length;

			singleBaseGame(view);
		}
	}

    public void simulate(String[] args) {
		System.out.println("Output will be on the screen!");
		System.out.println();
		System.out.println("Ctrl+C to abort simulation.");
		System.out.println();
		System.out.println("java Main -l1000");
		System.out.println("Do 1 000 iterations.");
		System.out.println();
		System.out.println("java Main -l1000k");
		System.out.println("Do 1 000 000 iterations.");
		System.out.println();
		System.out.println("java Main -l10m");
		System.out.println("Do 10 000 000 iterations.");
		System.out.println();
		System.out.println("java Main");
		System.out.println("Do 10 000 000 iterations as default value.");
		System.out.println();

		if (args.length > 0 && args[0].contains("-l")) {
			String lParameter = args[0].substring(2);

			if (lParameter.contains("k")) {
				lParameter = lParameter.substring(0, lParameter.length() - 1);
				lParameter += "000";
			}

			if (lParameter.contains("m")) {
				lParameter = lParameter.substring(0, lParameter.length() - 1);
				lParameter += "000000";
			}

			try {
				statistics.numberOfBaseGameSpins = Long.parseLong(lParameter);
			} catch (Exception exception) {
			}
		}

		simulate();

		// TODO Hunt for 96.00% RTP.
		System.out.println("Won money: " + statistics.wonMoney);
		System.out.println("Lost money: " + statistics.lostMoney);
		System.out.println("Total RTP%: " + ((double) statistics.wonMoney * 100.0 / (double) statistics.lostMoney));
		System.out.println("Base Game RTP%: " + ((double) statistics.baseMoney * 100.0 / (double) statistics.lostMoney));
		System.out.println("Base Hit Frequency: " + statistics.baseHitFrequency);
		System.out.println("Total Number of Games: " + statistics.totalNumberOfGames);
		System.out.println("Win Histogram: ");
		statistics.winHistogram.entrySet().stream().sorted(Map.Entry.comparingByKey())
           .forEach(entry -> System.out.println(entry.getKey() + "\t" + entry.getValue()));
	}
}