import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class App {
    private static final Random PRNG = ThreadLocalRandom.current();

    private static final double RTP_TARGET = 0.96;

    private static final int REEL_SIZE = 139;

    private static final String[] SYMBOLS = {"star", "roku", "one", "two", "three", "four", "five", "six"};

    private static final int SAMPLE_SIZE = 1000;

    private static final int APPROXIMATION_SIZE = 100000;

    private static final int SAMPLES_RATIO = APPROXIMATION_SIZE / SAMPLE_SIZE;

    private static final int POPULATION_SIZE = 100;

    private static final double MUTATION_RATE = 0.05;

    private static final long NUMBER_OF_OPTIMIZATION_INDIVIDUALS = 100000L;

    private static String[] RAW_CHUNKS_FREQUENCY = {
		"fivefivefive,0,fivefivefive,4,fivefivefive,0,fivefivefive,1,fivefivefive,2",
		"fivefivefour,3,fivefivefour,2,fivefivefour,2,fivefivefour,2,fivefivefour,2",
		"fivefiveone,2,fivefiveone,3,fivefiveone,3,fivefiveone,4,fivefiveone,1",
		"fivefiveroku,0,fivefiveroku,0,fivefiveroku,0,fivefiveroku,1,fivefiveroku,0",
		"fivefivesix,3,fivefivesix,6,fivefivesix,3,fivefivesix,0,fivefivesix,1",
		"fivefivestar,0,fivefivestar,0,fivefivestar,0,fivefivestar,1,fivefivestar,0",
		"fivefivethree,2,fivefivethree,6,fivefivethree,0,fivefivethree,3,fivefivethree,4",
		"fivefivetwo,0,fivefivetwo,1,fivefivetwo,4,fivefivetwo,5,fivefivetwo,4",
		"fivefourfive,5,fivefourfive,0,fivefourfive,3,fivefourfive,2,fivefourfive,2",
		"fivefourfour,4,fivefourfour,2,fivefourfour,3,fivefourfour,5,fivefourfour,9",
		"fivefourone,7,fivefourone,5,fivefourone,7,fivefourone,4,fivefourone,1",
		"fivefourroku,1,fivefourroku,1,fivefourroku,2,fivefourroku,0,fivefourroku,0",
		"fivefoursix,3,fivefoursix,3,fivefoursix,1,fivefoursix,4,fivefoursix,2",
		"fivefourstar,0,fivefourstar,0,fivefourstar,1,fivefourstar,0,fivefourstar,0",
		"fivefourthree,3,fivefourthree,10,fivefourthree,1,fivefourthree,3,fivefourthree,6",
		"fivefourtwo,4,fivefourtwo,2,fivefourtwo,3,fivefourtwo,8,fivefourtwo,4",
		"fiveonefive,2,fiveonefive,7,fiveonefive,3,fiveonefive,4,fiveonefive,1",
		"fiveonefour,4,fiveonefour,4,fiveonefour,3,fiveonefour,3,fiveonefour,5",
		"fiveoneone,5,fiveoneone,8,fiveoneone,5,fiveoneone,4,fiveoneone,6",
		"fiveoneroku,0,fiveoneroku,1,fiveoneroku,1,fiveoneroku,0,fiveoneroku,0",
		"fiveonesix,3,fiveonesix,6,fiveonesix,7,fiveonesix,2,fiveonesix,4",
		"fiveonestar,0,fiveonestar,2,fiveonestar,1,fiveonestar,2,fiveonestar,1",
		"fiveonethree,4,fiveonethree,1,fiveonethree,3,fiveonethree,2,fiveonethree,4",
		"fiveonetwo,4,fiveonetwo,6,fiveonetwo,6,fiveonetwo,6,fiveonetwo,4",
		"fiverokufive,1,fiverokufive,0,fiverokufive,0,fiverokufive,0,fiverokufive,0",
		"fiverokufour,0,fiverokufour,0,fiverokufour,0,fiverokufour,0,fiverokufour,2",
		"fiverokuone,1,fiverokuone,1,fiverokuone,0,fiverokuone,1,fiverokuone,0",
		"fiverokusix,0,fiverokusix,0,fiverokusix,2,fiverokusix,1,fiverokusix,0",
		"fiverokuthree,1,fiverokuthree,0,fiverokuthree,1,fiverokuthree,0,fiverokuthree,1",
		"fiverokutwo,0,fiverokutwo,2,fiverokutwo,0,fiverokutwo,0,fiverokutwo,0",
		"fivesixfive,1,fivesixfive,1,fivesixfive,1,fivesixfive,3,fivesixfive,3",
		"fivesixfour,2,fivesixfour,5,fivesixfour,0,fivesixfour,2,fivesixfour,2",
		"fivesixone,3,fivesixone,2,fivesixone,1,fivesixone,1,fivesixone,4",
		"fivesixroku,0,fivesixroku,1,fivesixroku,1,fivesixroku,0,fivesixroku,0",
		"fivesixsix,1,fivesixsix,4,fivesixsix,1,fivesixsix,1,fivesixsix,4",
		"fivesixstar,0,fivesixstar,1,fivesixstar,0,fivesixstar,0,fivesixstar,0",
		"fivesixthree,3,fivesixthree,2,fivesixthree,3,fivesixthree,6,fivesixthree,2",
		"fivesixtwo,3,fivesixtwo,5,fivesixtwo,5,fivesixtwo,3,fivesixtwo,4",
		"fivestarfive,1,fivestarfive,0,fivestarfive,0,fivestarfive,0,fivestarfive,0",
		"fivestarfour,0,fivestarfour,2,fivestarfour,0,fivestarfour,0,fivestarfour,0",
		"fivestarone,1,fivestarone,0,fivestarone,0,fivestarone,0,fivestarone,0",
		"fivestarsix,1,fivestarsix,0,fivestarsix,1,fivestarsix,2,fivestarsix,0",
		"fivestarthree,1,fivestarthree,0,fivestarthree,2,fivestarthree,1,fivestarthree,0",
		"fivestartwo,1,fivestartwo,1,fivestartwo,0,fivestartwo,1,fivestartwo,0",
		"fivethreefive,4,fivethreefive,3,fivethreefive,2,fivethreefive,0,fivethreefive,1",
		"fivethreefour,6,fivethreefour,2,fivethreefour,6,fivethreefour,2,fivethreefour,7",
		"fivethreeone,2,fivethreeone,3,fivethreeone,4,fivethreeone,2,fivethreeone,11",
		"fivethreesix,3,fivethreesix,3,fivethreesix,2,fivethreesix,1,fivethreesix,3",
		"fivethreestar,0,fivethreestar,0,fivethreestar,1,fivethreestar,0,fivethreestar,0",
		"fivethreethree,2,fivethreethree,4,fivethreethree,2,fivethreethree,2,fivethreethree,3",
		"fivethreetwo,6,fivethreetwo,0,fivethreetwo,3,fivethreetwo,3,fivethreetwo,4",
		"fivetwofive,4,fivetwofive,1,fivetwofive,2,fivetwofive,3,fivetwofive,5",
		"fivetwofour,4,fivetwofour,4,fivetwofour,4,fivetwofour,5,fivetwofour,7",
		"fivetwoone,3,fivetwoone,5,fivetwoone,7,fivetwoone,4,fivetwoone,5",
		"fivetworoku,0,fivetworoku,1,fivetworoku,0,fivetworoku,0,fivetworoku,1",
		"fivetwosix,5,fivetwosix,2,fivetwosix,4,fivetwosix,5,fivetwosix,2",
		"fivetwostar,0,fivetwostar,0,fivetwostar,1,fivetwostar,2,fivetwostar,0",
		"fivetwothree,3,fivetwothree,1,fivetwothree,2,fivetwothree,5,fivetwothree,5",
		"fivetwotwo,3,fivetwotwo,4,fivetwotwo,1,fivetwotwo,3,fivetwotwo,5",
		"fourfivefive,3,fourfivefive,2,fourfivefive,4,fourfivefive,5,fourfivefive,0",
		"fourfivefour,2,fourfivefour,4,fourfivefour,5,fourfivefour,3,fourfivefour,1",
		"fourfiveone,5,fourfiveone,3,fourfiveone,5,fourfiveone,5,fourfiveone,4",
		"fourfiveroku,0,fourfiveroku,0,fourfiveroku,1,fourfiveroku,2,fourfiveroku,0",
		"fourfivesix,0,fourfivesix,2,fourfivesix,2,fourfivesix,1,fourfivesix,1",
		"fourfivestar,0,fourfivestar,0,fourfivestar,0,fourfivestar,0,fourfivestar,1",
		"fourfivethree,5,fourfivethree,4,fourfivethree,5,fourfivethree,5,fourfivethree,6",
		"fourfivetwo,4,fourfivetwo,4,fourfivetwo,1,fourfivetwo,4,fourfivetwo,2",
		"fourfourfive,1,fourfourfive,5,fourfourfive,2,fourfourfive,5,fourfourfive,2",
		"fourfourfour,4,fourfourfour,5,fourfourfour,5,fourfourfour,6,fourfourfour,3",
		"fourfourone,9,fourfourone,6,fourfourone,4,fourfourone,7,fourfourone,0",
		"fourfourroku,0,fourfourroku,1,fourfourroku,1,fourfourroku,0,fourfourroku,1",
		"fourfoursix,3,fourfoursix,2,fourfoursix,7,fourfoursix,2,fourfoursix,3",
		"fourfourstar,0,fourfourstar,1,fourfourstar,1,fourfourstar,1,fourfourstar,0",
		"fourfourthree,7,fourfourthree,3,fourfourthree,0,fourfourthree,3,fourfourthree,3",
		"fourfourtwo,4,fourfourtwo,1,fourfourtwo,4,fourfourtwo,3,fourfourtwo,5",
		"fouronefive,7,fouronefive,7,fouronefive,3,fouronefive,8,fouronefive,2",
		"fouronefour,5,fouronefour,6,fouronefour,4,fouronefour,5,fouronefour,4",
		"fouroneone,5,fouroneone,6,fouroneone,10,fouroneone,3,fouroneone,7",
		"fouroneroku,0,fouroneroku,1,fouroneroku,0,fouroneroku,0,fouroneroku,1",
		"fouronesix,4,fouronesix,7,fouronesix,4,fouronesix,5,fouronesix,9",
		"fouronestar,1,fouronestar,1,fouronestar,0,fouronestar,1,fouronestar,2",
		"fouronethree,7,fouronethree,5,fouronethree,5,fouronethree,3,fouronethree,7",
		"fouronetwo,8,fouronetwo,7,fouronetwo,6,fouronetwo,8,fouronetwo,7",
		"fourrokufive,0,fourrokufive,0,fourrokufive,0,fourrokufive,1,fourrokufive,2",
		"fourrokufour,2,fourrokufour,1,fourrokufour,1,fourrokufour,0,fourrokufour,1",
		"fourrokuone,1,fourrokuone,0,fourrokuone,0,fourrokuone,1,fourrokuone,0",
		"fourrokusix,0,fourrokusix,0,fourrokusix,1,fourrokusix,0,fourrokusix,1",
		"fourrokustar,0,fourrokustar,0,fourrokustar,0,fourrokustar,1,fourrokustar,0",
		"fourrokuthree,1,fourrokuthree,0,fourrokuthree,1,fourrokuthree,1,fourrokuthree,1",
		"fourrokutwo,0,fourrokutwo,2,fourrokutwo,1,fourrokutwo,1,fourrokutwo,0",
		"foursixfive,3,foursixfive,2,foursixfive,2,foursixfive,3,foursixfive,4",
		"foursixfour,6,foursixfour,6,foursixfour,4,foursixfour,2,foursixfour,4",
		"foursixone,7,foursixone,0,foursixone,1,foursixone,3,foursixone,5",
		"foursixroku,0,foursixroku,0,foursixroku,1,foursixroku,0,foursixroku,0",
		"foursixsix,1,foursixsix,0,foursixsix,2,foursixsix,3,foursixsix,2",
		"foursixstar,0,foursixstar,0,foursixstar,1,foursixstar,0,foursixstar,0",
		"foursixthree,6,foursixthree,7,foursixthree,2,foursixthree,5,foursixthree,5",
		"foursixtwo,2,foursixtwo,2,foursixtwo,5,foursixtwo,5,foursixtwo,1",
		"fourstarfive,1,fourstarfive,1,fourstarfive,0,fourstarfive,0,fourstarfive,0",
		"fourstarfour,0,fourstarfour,1,fourstarfour,0,fourstarfour,1,fourstarfour,0",
		"fourstarone,0,fourstarone,1,fourstarone,0,fourstarone,1,fourstarone,1",
		"fourstarsix,0,fourstarsix,0,fourstarsix,1,fourstarsix,1,fourstarsix,0",
		"fourstarstar,0,fourstarstar,1,fourstarstar,0,fourstarstar,0,fourstarstar,0",
		"fourstarthree,0,fourstarthree,2,fourstarthree,0,fourstarthree,0,fourstarthree,0",
		"fourstartwo,1,fourstartwo,1,fourstartwo,1,fourstartwo,1,fourstartwo,0",
		"fourthreefive,8,fourthreefive,2,fourthreefive,1,fourthreefive,3,fourthreefive,1",
		"fourthreefour,5,fourthreefour,7,fourthreefour,5,fourthreefour,4,fourthreefour,3",
		"fourthreeone,8,fourthreeone,8,fourthreeone,6,fourthreeone,8,fourthreeone,2",
		"fourthreeroku,1,fourthreeroku,0,fourthreeroku,0,fourthreeroku,1,fourthreeroku,0",
		"fourthreesix,6,fourthreesix,1,fourthreesix,1,fourthreesix,6,fourthreesix,3",
		"fourthreestar,1,fourthreestar,1,fourthreestar,1,fourthreestar,0,fourthreestar,0",
		"fourthreethree,5,fourthreethree,3,fourthreethree,15,fourthreethree,4,fourthreethree,1",
		"fourthreetwo,3,fourthreetwo,5,fourthreetwo,12,fourthreetwo,3,fourthreetwo,10",
		"fourtwofive,2,fourtwofive,2,fourtwofive,1,fourtwofive,3,fourtwofive,2",
		"fourtwofour,5,fourtwofour,5,fourtwofour,7,fourtwofour,5,fourtwofour,4",
		"fourtwoone,6,fourtwoone,2,fourtwoone,5,fourtwoone,2,fourtwoone,9",
		"fourtworoku,1,fourtworoku,2,fourtworoku,0,fourtworoku,1,fourtworoku,0",
		"fourtwosix,1,fourtwosix,5,fourtwosix,2,fourtwosix,2,fourtwosix,4",
		"fourtwostar,1,fourtwostar,0,fourtwostar,1,fourtwostar,1,fourtwostar,0",
		"fourtwothree,6,fourtwothree,2,fourtwothree,2,fourtwothree,4,fourtwothree,5",
		"fourtwotwo,3,fourtwotwo,8,fourtwotwo,6,fourtwotwo,5,fourtwotwo,3",
		"onefivefive,5,onefivefive,1,onefivefive,2,onefivefive,3,onefivefive,1",
		"onefivefour,6,onefivefour,4,onefivefour,3,onefivefour,9,onefivefour,8",
		"onefiveone,6,onefiveone,5,onefiveone,9,onefiveone,7,onefiveone,4",
		"onefiveroku,1,onefiveroku,0,onefiveroku,0,onefiveroku,1,onefiveroku,1",
		"onefivesix,6,onefivesix,4,onefivesix,0,onefivesix,1,onefivesix,3",
		"onefivestar,0,onefivestar,1,onefivestar,0,onefivestar,1,onefivestar,0",
		"onefivethree,9,onefivethree,3,onefivethree,4,onefivethree,5,onefivethree,6",
		"onefivetwo,1,onefivetwo,7,onefivetwo,0,onefivetwo,4,onefivetwo,2",
		"onefourfive,4,onefourfive,5,onefourfive,2,onefourfive,2,onefourfive,3",
		"onefourfour,5,onefourfour,6,onefourfour,6,onefourfour,6,onefourfour,4",
		"onefourone,7,onefourone,5,onefourone,11,onefourone,3,onefourone,6",
		"onefourroku,1,onefourroku,1,onefourroku,0,onefourroku,0,onefourroku,2",
		"onefoursix,7,onefoursix,6,onefoursix,1,onefoursix,5,onefoursix,4",
		"onefourstar,2,onefourstar,1,onefourstar,0,onefourstar,0,onefourstar,1",
		"onefourthree,4,onefourthree,3,onefourthree,10,onefourthree,1,onefourthree,3",
		"onefourtwo,7,onefourtwo,5,onefourtwo,2,onefourtwo,10,onefourtwo,3",
		"oneonefive,4,oneonefive,6,oneonefive,5,oneonefive,9,oneonefive,2",
		"oneonefour,3,oneonefour,9,oneonefour,7,oneonefour,6,oneonefour,7",
		"oneoneone,6,oneoneone,8,oneoneone,6,oneoneone,7,oneoneone,9",
		"oneoneroku,1,oneoneroku,2,oneoneroku,1,oneoneroku,0,oneoneroku,1",
		"oneonesix,4,oneonesix,8,oneonesix,6,oneonesix,9,oneonesix,2",
		"oneonestar,2,oneonestar,0,oneonestar,0,oneonestar,0,oneonestar,4",
		"oneonethree,3,oneonethree,4,oneonethree,5,oneonethree,7,oneonethree,7",
		"oneonetwo,3,oneonetwo,8,oneonetwo,6,oneonetwo,9,oneonetwo,5",
		"onerokufive,1,onerokufive,0,onerokufive,2,onerokufive,0,onerokufive,0",
		"onerokufour,0,onerokufour,1,onerokufour,0,onerokufour,0,onerokufour,2",
		"onerokuone,1,onerokuone,1,onerokuone,0,onerokuone,4,onerokuone,1",
		"onerokusix,0,onerokusix,1,onerokusix,0,onerokusix,0,onerokusix,2",
		"onerokustar,0,onerokustar,0,onerokustar,0,onerokustar,1,onerokustar,0",
		"onerokuthree,1,onerokuthree,1,onerokuthree,1,onerokuthree,2,onerokuthree,1",
		"onerokutwo,0,onerokutwo,1,onerokutwo,0,onerokutwo,1,onerokutwo,1",
		"onesixfive,5,onesixfive,4,onesixfive,1,onesixfive,3,onesixfive,1",
		"onesixfour,6,onesixfour,9,onesixfour,8,onesixfour,3,onesixfour,8",
		"onesixone,6,onesixone,6,onesixone,5,onesixone,10,onesixone,7",
		"onesixroku,0,onesixroku,2,onesixroku,0,onesixroku,2,onesixroku,0",
		"onesixsix,4,onesixsix,1,onesixsix,0,onesixsix,8,onesixsix,5",
		"onesixstar,0,onesixstar,1,onesixstar,1,onesixstar,0,onesixstar,1",
		"onesixthree,5,onesixthree,3,onesixthree,4,onesixthree,10,onesixthree,6",
		"onesixtwo,4,onesixtwo,4,onesixtwo,3,onesixtwo,9,onesixtwo,9",
		"onestarfive,1,onestarfive,0,onestarfive,0,onestarfive,0,onestarfive,0",
		"onestarfour,0,onestarfour,1,onestarfour,1,onestarfour,1,onestarfour,0",
		"onestarone,1,onestarone,1,onestarone,1,onestarone,0,onestarone,1",
		"onestarroku,0,onestarroku,0,onestarroku,0,onestarroku,1,onestarroku,1",
		"onestarsix,0,onestarsix,0,onestarsix,1,onestarsix,1,onestarsix,0",
		"onestarthree,1,onestarthree,2,onestarthree,0,onestarthree,1,onestarthree,2",
		"onestartwo,0,onestartwo,0,onestartwo,1,onestartwo,1,onestartwo,1",
		"onethreefive,6,onethreefive,3,onethreefive,2,onethreefive,6,onethreefive,4",
		"onethreefour,3,onethreefour,4,onethreefour,7,onethreefour,4,onethreefour,5",
		"onethreeone,12,onethreeone,7,onethreeone,7,onethreeone,5,onethreeone,2",
		"onethreeroku,0,onethreeroku,1,onethreeroku,0,onethreeroku,0,onethreeroku,1",
		"onethreesix,5,onethreesix,2,onethreesix,2,onethreesix,5,onethreesix,5",
		"onethreestar,1,onethreestar,0,onethreestar,0,onethreestar,0,onethreestar,3",
		"onethreethree,2,onethreethree,6,onethreethree,4,onethreethree,6,onethreethree,4",
		"onethreetwo,9,onethreetwo,9,onethreetwo,3,onethreetwo,2,onethreetwo,5",
		"onetwofive,2,onetwofive,2,onetwofive,5,onetwofive,4,onetwofive,7",
		"onetwofour,4,onetwofour,11,onetwofour,2,onetwofour,7,onetwofour,4",
		"onetwoone,10,onetwoone,4,onetwoone,10,onetwoone,4,onetwoone,12",
		"onetworoku,1,onetworoku,1,onetworoku,1,onetworoku,1,onetworoku,1",
		"onetwosix,5,onetwosix,5,onetwosix,5,onetwosix,3,onetwosix,7",
		"onetwostar,0,onetwostar,1,onetwostar,0,onetwostar,0,onetwostar,2",
		"onetwothree,7,onetwothree,4,onetwothree,7,onetwothree,6,onetwothree,8",
		"onetwotwo,6,onetwotwo,7,onetwotwo,6,onetwotwo,3,onetwotwo,11",
		"rokufivefour,1,rokufivefour,0,rokufivefour,0,rokufivefour,3,rokufivefour,0",
		"rokufiveone,0,rokufiveone,2,rokufiveone,0,rokufiveone,1,rokufiveone,1",
		"rokufivesix,1,rokufivesix,0,rokufivesix,1,rokufivesix,0,rokufivesix,0",
		"rokufivestar,0,rokufivestar,0,rokufivestar,1,rokufivestar,0,rokufivestar,0",
		"rokufivethree,1,rokufivethree,0,rokufivethree,0,rokufivethree,0,rokufivethree,0",
		"rokufivetwo,0,rokufivetwo,1,rokufivetwo,0,rokufivetwo,0,rokufivetwo,0",
		"rokufourfive,2,rokufourfive,1,rokufourfive,0,rokufourfive,1,rokufourfive,0",
		"rokufourfour,0,rokufourfour,1,rokufourfour,0,rokufourfour,0,rokufourfour,1",
		"rokufourone,3,rokufourone,1,rokufourone,0,rokufourone,0,rokufourone,1",
		"rokufoursix,1,rokufoursix,0,rokufoursix,1,rokufoursix,0,rokufoursix,2",
		"rokufourthree,0,rokufourthree,1,rokufourthree,2,rokufourthree,0,rokufourthree,1",
		"rokufourtwo,0,rokufourtwo,0,rokufourtwo,0,rokufourtwo,2,rokufourtwo,0",
		"rokuonefive,1,rokuonefive,0,rokuonefive,0,rokuonefive,1,rokuonefive,2",
		"rokuonefour,1,rokuonefour,0,rokuonefour,0,rokuonefour,1,rokuonefour,2",
		"rokuoneone,1,rokuoneone,3,rokuoneone,1,rokuoneone,0,rokuoneone,2",
		"rokuoneroku,0,rokuoneroku,0,rokuoneroku,0,rokuoneroku,0,rokuoneroku,1",
		"rokuonesix,0,rokuonesix,0,rokuonesix,0,rokuonesix,1,rokuonesix,0",
		"rokuonethree,1,rokuonethree,2,rokuonethree,3,rokuonethree,0,rokuonethree,1",
		"rokuonetwo,1,rokuonetwo,1,rokuonetwo,2,rokuonetwo,0,rokuonetwo,1",
		"rokurokufive,0,rokurokufive,0,rokurokufive,1,rokurokufive,0,rokurokufive,1",
		"rokurokuone,0,rokurokuone,0,rokurokuone,1,rokurokuone,0,rokurokuone,0",
		"rokurokusix,0,rokurokusix,0,rokurokusix,0,rokurokusix,1,rokurokusix,0",
		"rokusixfive,0,rokusixfive,0,rokusixfive,1,rokusixfive,1,rokusixfive,1",
		"rokusixfour,1,rokusixfour,2,rokusixfour,1,rokusixfour,1,rokusixfour,0",
		"rokusixone,1,rokusixone,1,rokusixone,2,rokusixone,0,rokusixone,0",
		"rokusixsix,0,rokusixsix,0,rokusixsix,0,rokusixsix,0,rokusixsix,1",
		"rokusixthree,1,rokusixthree,0,rokusixthree,0,rokusixthree,0,rokusixthree,0",
		"rokusixtwo,2,rokusixtwo,0,rokusixtwo,1,rokusixtwo,0,rokusixtwo,0",
		"rokustarfive,0,rokustarfive,0,rokustarfive,0,rokustarfive,0,rokustarfive,1",
		"rokustarfour,0,rokustarfour,0,rokustarfour,0,rokustarfour,1,rokustarfour,0",
		"rokustarsix,0,rokustarsix,0,rokustarsix,0,rokustarsix,0,rokustarsix,1",
		"rokuthreefive,1,rokuthreefive,1,rokuthreefive,2,rokuthreefive,1,rokuthreefive,0",
		"rokuthreefour,0,rokuthreefour,1,rokuthreefour,1,rokuthreefour,0,rokuthreefour,2",
		"rokuthreeone,2,rokuthreeone,0,rokuthreeone,1,rokuthreeone,0,rokuthreeone,0",
		"rokuthreesix,0,rokuthreesix,1,rokuthreesix,0,rokuthreesix,0,rokuthreesix,0",
		"rokuthreethree,0,rokuthreethree,1,rokuthreethree,0,rokuthreethree,1,rokuthreethree,0",
		"rokuthreetwo,1,rokuthreetwo,0,rokuthreetwo,0,rokuthreetwo,1,rokuthreetwo,0",
		"rokutwofive,1,rokutwofive,0,rokutwofive,0,rokutwofive,2,rokutwofive,2",
		"rokutwoone,1,rokutwoone,0,rokutwoone,0,rokutwoone,2,rokutwoone,0",
		"rokutwothree,0,rokutwothree,0,rokutwothree,4,rokutwothree,0,rokutwothree,0",
		"rokutwotwo,0,rokutwotwo,1,rokutwotwo,1,rokutwotwo,1,rokutwotwo,0",
		"sixfivefive,2,sixfivefive,2,sixfivefive,5,sixfivefive,0,sixfivefive,1",
		"sixfivefour,1,sixfivefour,1,sixfivefour,6,sixfivefour,2,sixfivefour,2",
		"sixfiveone,3,sixfiveone,0,sixfiveone,2,sixfiveone,3,sixfiveone,3",
		"sixfiveroku,0,sixfiveroku,0,sixfiveroku,1,sixfiveroku,0,sixfiveroku,0",
		"sixfivesix,2,sixfivesix,6,sixfivesix,3,sixfivesix,2,sixfivesix,3",
		"sixfivestar,1,sixfivestar,2,sixfivestar,0,sixfivestar,1,sixfivestar,1",
		"sixfivethree,1,sixfivethree,3,sixfivethree,1,sixfivethree,7,sixfivethree,2",
		"sixfivetwo,2,sixfivetwo,2,sixfivetwo,5,sixfivetwo,1,sixfivetwo,4",
		"sixfourfive,5,sixfourfive,4,sixfourfive,5,sixfourfive,4,sixfourfive,2",
		"sixfourfour,2,sixfourfour,5,sixfourfour,8,sixfourfour,4,sixfourfour,0",
		"sixfourone,0,sixfourone,9,sixfourone,4,sixfourone,3,sixfourone,3",
		"sixfourroku,0,sixfourroku,0,sixfourroku,0,sixfourroku,1,sixfourroku,0",
		"sixfoursix,4,sixfoursix,2,sixfoursix,2,sixfoursix,3,sixfoursix,4",
		"sixfourstar,0,sixfourstar,1,sixfourstar,1,sixfourstar,1,sixfourstar,1",
		"sixfourthree,4,sixfourthree,5,sixfourthree,4,sixfourthree,2,sixfourthree,4",
		"sixfourtwo,9,sixfourtwo,9,sixfourtwo,4,sixfourtwo,3,sixfourtwo,3",
		"sixonefive,1,sixonefive,4,sixonefive,7,sixonefive,3,sixonefive,2",
		"sixonefour,5,sixonefour,3,sixonefour,4,sixonefour,6,sixonefour,4",
		"sixoneone,2,sixoneone,3,sixoneone,5,sixoneone,3,sixoneone,4",
		"sixoneroku,0,sixoneroku,0,sixoneroku,1,sixoneroku,0,sixoneroku,0",
		"sixonesix,4,sixonesix,2,sixonesix,2,sixonesix,6,sixonesix,2",
		"sixonethree,4,sixonethree,6,sixonethree,4,sixonethree,3,sixonethree,5",
		"sixonetwo,1,sixonetwo,4,sixonetwo,6,sixonetwo,1,sixonetwo,5",
		"sixrokufive,0,sixrokufive,1,sixrokufive,0,sixrokufive,0,sixrokufive,0",
		"sixrokufour,1,sixrokufour,1,sixrokufour,1,sixrokufour,0,sixrokufour,1",
		"sixrokuone,1,sixrokuone,0,sixrokuone,1,sixrokuone,1,sixrokuone,0",
		"sixrokuroku,0,sixrokuroku,0,sixrokuroku,1,sixrokuroku,0,sixrokuroku,0",
		"sixrokusix,0,sixrokusix,0,sixrokusix,1,sixrokusix,2,sixrokusix,2",
		"sixrokuthree,0,sixrokuthree,0,sixrokuthree,1,sixrokuthree,2,sixrokuthree,2",
		"sixrokutwo,2,sixrokutwo,0,sixrokutwo,0,sixrokutwo,2,sixrokutwo,0",
		"sixsixfive,1,sixsixfive,1,sixsixfive,3,sixsixfive,0,sixsixfive,1",
		"sixsixfour,3,sixsixfour,1,sixsixfour,3,sixsixfour,5,sixsixfour,3",
		"sixsixone,3,sixsixone,1,sixsixone,4,sixsixone,3,sixsixone,2",
		"sixsixroku,0,sixsixroku,0,sixsixroku,0,sixsixroku,1,sixsixroku,0",
		"sixsixsix,2,sixsixsix,1,sixsixsix,1,sixsixsix,1,sixsixsix,1",
		"sixsixstar,0,sixsixstar,0,sixsixstar,0,sixsixstar,1,sixsixstar,0",
		"sixsixthree,0,sixsixthree,2,sixsixthree,4,sixsixthree,2,sixsixthree,1",
		"sixsixtwo,2,sixsixtwo,1,sixsixtwo,5,sixsixtwo,4,sixsixtwo,2",
		"sixstarfive,2,sixstarfive,0,sixstarfive,0,sixstarfive,0,sixstarfive,0",
		"sixstarfour,0,sixstarfour,1,sixstarfour,0,sixstarfour,1,sixstarfour,0",
		"sixstarone,1,sixstarone,0,sixstarone,2,sixstarone,2,sixstarone,0",
		"sixstarsix,0,sixstarsix,0,sixstarsix,0,sixstarsix,1,sixstarsix,0",
		"sixstarthree,0,sixstarthree,3,sixstarthree,0,sixstarthree,1,sixstarthree,1",
		"sixthreefive,1,sixthreefive,4,sixthreefive,5,sixthreefive,4,sixthreefive,3",
		"sixthreefour,6,sixthreefour,4,sixthreefour,2,sixthreefour,1,sixthreefour,4",
		"sixthreeone,6,sixthreeone,1,sixthreeone,2,sixthreeone,7,sixthreeone,1",
		"sixthreeroku,1,sixthreeroku,0,sixthreeroku,0,sixthreeroku,0,sixthreeroku,2",
		"sixthreesix,3,sixthreesix,1,sixthreesix,1,sixthreesix,2,sixthreesix,2",
		"sixthreestar,1,sixthreestar,0,sixthreestar,0,sixthreestar,1,sixthreestar,2",
		"sixthreethree,2,sixthreethree,3,sixthreethree,2,sixthreethree,0,sixthreethree,2",
		"sixthreetwo,1,sixthreetwo,3,sixthreetwo,6,sixthreetwo,4,sixthreetwo,4",
		"sixtwofive,3,sixtwofive,3,sixtwofive,4,sixtwofive,4,sixtwofive,1",
		"sixtwofour,5,sixtwofour,4,sixtwofour,3,sixtwofour,4,sixtwofour,6",
		"sixtwoone,3,sixtwoone,4,sixtwoone,4,sixtwoone,3,sixtwoone,4",
		"sixtworoku,1,sixtworoku,0,sixtworoku,0,sixtworoku,1,sixtworoku,0",
		"sixtwosix,3,sixtwosix,2,sixtwosix,5,sixtwosix,1,sixtwosix,3",
		"sixtwostar,0,sixtwostar,1,sixtwostar,1,sixtwostar,0,sixtwostar,2",
		"sixtwothree,7,sixtwothree,3,sixtwothree,2,sixtwothree,3,sixtwothree,2",
		"sixtwotwo,3,sixtwotwo,3,sixtwotwo,4,sixtwotwo,4,sixtwotwo,3",
		"starfivefive,0,starfivefive,0,starfivefive,1,starfivefive,1,starfivefive,0",
		"starfivefour,3,starfivefour,0,starfivefour,1,starfivefour,0,starfivefour,0",
		"starfiveone,0,starfiveone,0,starfiveone,2,starfiveone,0,starfiveone,0",
		"starfivethree,1,starfivethree,0,starfivethree,0,starfivethree,0,starfivethree,0",
		"starfourfive,1,starfourfive,0,starfourfive,0,starfourfive,0,starfourfive,1",
		"starfourfour,1,starfourfour,0,starfourfour,0,starfourfour,0,starfourfour,1",
		"starfourone,1,starfourone,0,starfourone,1,starfourone,1,starfourone,1",
		"starfourroku,0,starfourroku,0,starfourroku,1,starfourroku,0,starfourroku,0",
		"starfoursix,0,starfoursix,1,starfoursix,0,starfoursix,1,starfoursix,1",
		"starfourthree,1,starfourthree,0,starfourthree,0,starfourthree,0,starfourthree,0",
		"starfourtwo,1,starfourtwo,0,starfourtwo,0,starfourtwo,0,starfourtwo,1",
		"staronefive,0,staronefive,0,staronefive,2,staronefive,0,staronefive,0",
		"staronefour,0,staronefour,0,staronefour,0,staronefour,0,staronefour,1",
		"staroneone,1,staroneone,0,staroneone,0,staroneone,0,staroneone,0",
		"staronesix,1,staronesix,0,staronesix,1,staronesix,1,staronesix,0",
		"staronethree,0,staronethree,0,staronethree,1,staronethree,0,staronethree,0",
		"staronetwo,0,staronetwo,0,staronetwo,3,staronetwo,0,staronetwo,0",
		"starrokuroku,0,starrokuroku,0,starrokuroku,1,starrokuroku,0,starrokuroku,0",
		"starrokuthree,1,starrokuthree,0,starrokuthree,1,starrokuthree,0,starrokuthree,0",
		"starrokutwo,1,starrokutwo,0,starrokutwo,0,starrokutwo,0,starrokutwo,0",
		"starsixfive,0,starsixfive,1,starsixfive,0,starsixfive,1,starsixfive,0",
		"starsixfour,1,starsixfour,0,starsixfour,0,starsixfour,0,starsixfour,0",
		"starsixone,0,starsixone,1,starsixone,0,starsixone,0,starsixone,0",
		"starsixthree,0,starsixthree,0,starsixthree,1,starsixthree,1,starsixthree,1",
		"starsixtwo,0,starsixtwo,0,starsixtwo,0,starsixtwo,0,starsixtwo,2",
		"starstarsix,0,starstarsix,0,starstarsix,1,starstarsix,0,starstarsix,0",
		"starthreefive,0,starthreefive,0,starthreefive,0,starthreefive,1,starthreefive,1",
		"starthreefour,1,starthreefour,2,starthreefour,0,starthreefour,0,starthreefour,0",
		"starthreeone,1,starthreeone,2,starthreeone,0,starthreeone,2,starthreeone,1",
		"starthreeroku,1,starthreeroku,0,starthreeroku,0,starthreeroku,0,starthreeroku,0",
		"starthreestar,1,starthreestar,0,starthreestar,0,starthreestar,0,starthreestar,0",
		"starthreethree,1,starthreethree,1,starthreethree,1,starthreethree,2,starthreethree,1",
		"startwofour,1,startwofour,0,startwofour,0,startwofour,0,startwofour,1",
		"startwoone,0,startwoone,2,startwoone,0,startwoone,0,startwoone,0",
		"startwosix,1,startwosix,0,startwosix,0,startwosix,0,startwosix,0",
		"startwothree,1,startwothree,0,startwothree,0,startwothree,1,startwothree,0",
		"startwotwo,0,startwotwo,1,startwotwo,0,startwotwo,1,startwotwo,0",
		"threefivefive,2,threefivefive,4,threefivefive,2,threefivefive,2,threefivefive,7",
		"threefivefour,5,threefivefour,5,threefivefour,5,threefivefour,4,threefivefour,3",
		"threefiveone,7,threefiveone,6,threefiveone,7,threefiveone,8,threefiveone,5",
		"threefiveroku,0,threefiveroku,0,threefiveroku,0,threefiveroku,0,threefiveroku,1",
		"threefivesix,3,threefivesix,2,threefivesix,4,threefivesix,3,threefivesix,2",
		"threefivestar,0,threefivestar,1,threefivestar,0,threefivestar,0,threefivestar,0",
		"threefivethree,5,threefivethree,1,threefivethree,9,threefivethree,4,threefivethree,2",
		"threefivetwo,3,threefivetwo,5,threefivetwo,2,threefivetwo,4,threefivetwo,5",
		"threefourfive,3,threefourfive,3,threefourfive,4,threefourfive,2,threefourfive,6",
		"threefourfour,2,threefourfour,5,threefourfour,8,threefourfour,5,threefourfour,3",
		"threefourone,6,threefourone,7,threefourone,4,threefourone,9,threefourone,6",
		"threefourroku,1,threefourroku,2,threefourroku,0,threefourroku,0,threefourroku,0",
		"threefoursix,4,threefoursix,3,threefoursix,1,threefoursix,2,threefoursix,4",
		"threefourstar,0,threefourstar,1,threefourstar,4,threefourstar,0,threefourstar,0",
		"threefourthree,3,threefourthree,5,threefourthree,4,threefourthree,0,threefourthree,4",
		"threefourtwo,5,threefourtwo,4,threefourtwo,2,threefourtwo,6,threefourtwo,5",
		"threeonefive,1,threeonefive,4,threeonefive,4,threeonefive,5,threeonefive,2",
		"threeonefour,8,threeonefour,4,threeonefour,6,threeonefour,3,threeonefour,3",
		"threeoneone,4,threeoneone,2,threeoneone,7,threeoneone,4,threeoneone,11",
		"threeoneroku,0,threeoneroku,2,threeoneroku,1,threeoneroku,2,threeoneroku,3",
		"threeonesix,1,threeonesix,4,threeonesix,6,threeonesix,3,threeonesix,4",
		"threeonestar,0,threeonestar,2,threeonestar,2,threeonestar,2,threeonestar,0",
		"threeonethree,7,threeonethree,5,threeonethree,8,threeonethree,3,threeonethree,5",
		"threeonetwo,8,threeonetwo,3,threeonetwo,8,threeonetwo,4,threeonetwo,7",
		"threerokufive,0,threerokufive,0,threerokufive,0,threerokufive,0,threerokufive,1",
		"threerokufour,1,threerokufour,0,threerokufour,2,threerokufour,1,threerokufour,0",
		"threerokuone,1,threerokuone,1,threerokuone,2,threerokuone,1,threerokuone,1",
		"threerokusix,0,threerokusix,0,threerokusix,0,threerokusix,1,threerokusix,0",
		"threerokuthree,0,threerokuthree,1,threerokuthree,1,threerokuthree,1,threerokuthree,0",
		"threerokutwo,0,threerokutwo,0,threerokutwo,2,threerokutwo,0,threerokutwo,1",
		"threesixfive,1,threesixfive,2,threesixfive,4,threesixfive,2,threesixfive,2",
		"threesixfour,4,threesixfour,2,threesixfour,6,threesixfour,6,threesixfour,3",
		"threesixone,4,threesixone,4,threesixone,4,threesixone,5,threesixone,6",
		"threesixroku,0,threesixroku,1,threesixroku,0,threesixroku,0,threesixroku,0",
		"threesixsix,4,threesixsix,1,threesixsix,0,threesixsix,3,threesixsix,2",
		"threesixthree,2,threesixthree,5,threesixthree,0,threesixthree,5,threesixthree,5",
		"threesixtwo,7,threesixtwo,2,threesixtwo,2,threesixtwo,3,threesixtwo,2",
		"threestarfive,0,threestarfive,1,threestarfive,0,threestarfive,0,threestarfive,1",
		"threestarfour,1,threestarfour,2,threestarfour,0,threestarfour,1,threestarfour,1",
		"threestarone,0,threestarone,2,threestarone,1,threestarone,1,threestarone,0",
		"threestarroku,0,threestarroku,0,threestarroku,0,threestarroku,2,threestarroku,0",
		"threestarsix,1,threestarsix,0,threestarsix,1,threestarsix,0,threestarsix,0",
		"threestarthree,0,threestarthree,0,threestarthree,1,threestarthree,0,threestarthree,0",
		"threestartwo,1,threestartwo,0,threestartwo,1,threestartwo,0,threestartwo,1",
		"threethreefive,3,threethreefive,3,threethreefive,2,threethreefive,1,threethreefive,4",
		"threethreefour,6,threethreefour,7,threethreefour,0,threethreefour,5,threethreefour,3",
		"threethreeone,10,threethreeone,5,threethreeone,4,threethreeone,4,threethreeone,5",
		"threethreeroku,1,threethreeroku,0,threethreeroku,0,threethreeroku,1,threethreeroku,0",
		"threethreesix,6,threethreesix,3,threethreesix,2,threethreesix,4,threethreesix,4",
		"threethreestar,0,threethreestar,1,threethreestar,0,threethreestar,1,threethreestar,0",
		"threethreethree,2,threethreethree,7,threethreethree,6,threethreethree,7,threethreethree,5",
		"threethreetwo,4,threethreetwo,3,threethreetwo,6,threethreetwo,6,threethreetwo,3",
		"threetwofive,1,threetwofive,3,threetwofive,3,threetwofive,2,threetwofive,7",
		"threetwofour,7,threetwofour,6,threetwofour,8,threetwofour,1,threetwofour,2",
		"threetwoone,7,threetwoone,4,threetwoone,4,threetwoone,4,threetwoone,6",
		"threetworoku,1,threetworoku,2,threetworoku,1,threetworoku,0,threetworoku,0",
		"threetwosix,3,threetwosix,5,threetwosix,6,threetwosix,3,threetwosix,5",
		"threetwostar,1,threetwostar,0,threetwostar,1,threetwostar,0,threetwostar,1",
		"threetwothree,3,threetwothree,4,threetwothree,5,threetwothree,4,threetwothree,4",
		"threetwotwo,5,threetwotwo,1,threetwotwo,1,threetwotwo,8,threetwotwo,7",
		"twofivefive,2,twofivefive,3,twofivefive,3,twofivefive,2,twofivefive,3",
		"twofivefour,3,twofivefour,3,twofivefour,3,twofivefour,1,twofivefour,2",
		"twofiveone,7,twofiveone,2,twofiveone,5,twofiveone,4,twofiveone,5",
		"twofiveroku,2,twofiveroku,0,twofiveroku,1,twofiveroku,0,twofiveroku,1",
		"twofivesix,3,twofivesix,2,twofivesix,6,twofivesix,2,twofivesix,1",
		"twofivestar,0,twofivestar,1,twofivestar,0,twofivestar,0,twofivestar,0",
		"twofivethree,2,twofivethree,4,twofivethree,3,twofivethree,6,twofivethree,5",
		"twofivetwo,4,twofivetwo,3,twofivetwo,1,twofivetwo,5,twofivetwo,6",
		"twofourfive,2,twofourfive,3,twofourfive,1,twofourfive,2,twofourfive,7",
		"twofourfour,9,twofourfour,3,twofourfour,6,twofourfour,6,twofourfour,2",
		"twofourone,8,twofourone,5,twofourone,4,twofourone,4,twofourone,4",
		"twofourroku,1,twofourroku,0,twofourroku,1,twofourroku,1,twofourroku,0",
		"twofoursix,4,twofoursix,2,twofoursix,4,twofoursix,3,twofoursix,2",
		"twofourstar,0,twofourstar,2,twofourstar,0,twofourstar,2,twofourstar,0",
		"twofourthree,1,twofourthree,5,twofourthree,8,twofourthree,6,twofourthree,5",
		"twofourtwo,0,twofourtwo,6,twofourtwo,5,twofourtwo,2,twofourtwo,4",
		"twoonefive,4,twoonefive,5,twoonefive,7,twoonefive,4,twoonefive,3",
		"twoonefour,10,twoonefour,9,twoonefour,6,twoonefour,5,twoonefour,7",
		"twooneone,5,twooneone,10,twooneone,5,twooneone,5,twooneone,11",
		"twooneroku,2,twooneroku,3,twooneroku,1,twooneroku,1,twooneroku,1",
		"twoonesix,6,twoonesix,3,twoonesix,4,twoonesix,1,twoonesix,2",
		"twoonestar,1,twoonestar,0,twoonestar,1,twoonestar,0,twoonestar,1",
		"twoonethree,7,twoonethree,3,twoonethree,4,twoonethree,6,twoonethree,7",
		"twoonetwo,5,twoonetwo,7,twoonetwo,6,twoonetwo,3,twoonetwo,5",
		"tworokufive,1,tworokufive,1,tworokufive,1,tworokufive,0,tworokufive,0",
		"tworokufour,0,tworokufour,1,tworokufour,0,tworokufour,0,tworokufour,2",
		"tworokuone,1,tworokuone,1,tworokuone,0,tworokuone,1,tworokuone,2",
		"tworokusix,1,tworokusix,0,tworokusix,1,tworokusix,1,tworokusix,1",
		"tworokustar,1,tworokustar,0,tworokustar,0,tworokustar,0,tworokustar,0",
		"tworokuthree,1,tworokuthree,0,tworokuthree,1,tworokuthree,1,tworokuthree,0",
		"tworokutwo,1,tworokutwo,1,tworokutwo,1,tworokutwo,0,tworokutwo,0",
		"twosixfive,0,twosixfive,3,twosixfive,2,twosixfive,2,twosixfive,1",
		"twosixfour,4,twosixfour,3,twosixfour,3,twosixfour,3,twosixfour,5",
		"twosixone,6,twosixone,4,twosixone,5,twosixone,4,twosixone,4",
		"twosixroku,0,twosixroku,3,twosixroku,0,twosixroku,3,twosixroku,0",
		"twosixsix,1,twosixsix,3,twosixsix,3,twosixsix,4,twosixsix,2",
		"twosixstar,0,twosixstar,0,twosixstar,0,twosixstar,1,twosixstar,0",
		"twosixthree,4,twosixthree,1,twosixthree,1,twosixthree,4,twosixthree,5",
		"twosixtwo,2,twosixtwo,4,twosixtwo,5,twosixtwo,1,twosixtwo,3",
		"twostarfour,1,twostarfour,1,twostarfour,0,twostarfour,0,twostarfour,0",
		"twostarone,1,twostarone,1,twostarone,2,twostarone,0,twostarone,0",
		"twostarsix,0,twostarsix,0,twostarsix,0,twostarsix,0,twostarsix,1",
		"twostarthree,1,twostarthree,0,twostarthree,0,twostarthree,1,twostarthree,0",
		"twostartwo,5,twostartwo,2,twostartwo,0,twostartwo,0,twostartwo,1",
		"twothreefive,5,twothreefive,7,twothreefive,7,twothreefive,9,twothreefive,3",
		"twothreefour,4,twothreefour,5,twothreefour,5,twothreefour,7,twothreefour,8",
		"twothreeone,5,twothreeone,9,twothreeone,7,twothreeone,4,twothreeone,4",
		"twothreeroku,2,twothreeroku,1,twothreeroku,0,twothreeroku,2,twothreeroku,0",
		"twothreesix,2,twothreesix,5,twothreesix,0,twothreesix,4,twothreesix,2",
		"twothreestar,0,twothreestar,1,twothreestar,1,twothreestar,0,twothreestar,0",
		"twothreethree,4,twothreethree,1,twothreethree,3,twothreethree,5,twothreethree,5",
		"twothreetwo,3,twothreetwo,3,twothreetwo,5,twothreetwo,7,twothreetwo,9",
		"twotwofive,2,twotwofive,1,twotwofive,3,twotwofive,6,twotwofive,3",
		"twotwofour,4,twotwofour,6,twotwofour,4,twotwofour,7,twotwofour,9",
		"twotwoone,6,twotwoone,11,twotwoone,7,twotwoone,7,twotwoone,4",
		"twotworoku,0,twotworoku,1,twotworoku,0,twotworoku,2,twotworoku,2",
		"twotwosix,2,twotwosix,5,twotwosix,4,twotwosix,2,twotwosix,3",
		"twotwostar,1,twotwostar,0,twotwostar,0,twotwostar,1,twotwostar,0",
		"twotwothree,3,twotwothree,2,twotwothree,6,twotwothree,2,twotwothree,5",
		"twotwotwo,1,twotwotwo,6,twotwotwo,6,twotwotwo,7,twotwotwo,6",
    };

    private static int[][][] RAW_SYMBOLS_FREQUENCY = {
        {
            {22, 24, 15, 11, 27},
            {24, 18, 18, 21, 13},
            {24, 22, 13, 14, 23}
        },
        {
            {25, 24, 20, 21, 19},
            {30, 27, 30, 19, 22},
            {30, 27, 24, 29, 21}
        },
        {
            {206, 178, 222, 208, 214},
            {194, 176, 215, 207, 219},
            {180, 186, 218, 205, 195}
        },
        {
            {163, 159, 156, 177, 160},
            {168, 168, 168, 169, 169},
            {161, 178, 174, 197, 180}
        },
        {
            {165, 185, 159, 158, 160},
            {150, 174, 157, 170, 158},
            {161, 165, 170, 158, 170}
        },
        {
            {177, 176, 181, 163, 175},
            {189, 168, 166, 175, 167},
            {156, 165, 145, 146, 173}
        },
        {
            {125, 130, 119, 140, 127},
            {123, 121, 133, 122, 125},
            {139, 131, 144, 119, 114}
        },
        {
            {117, 124, 128, 122, 118},
            {122, 148, 113, 117, 127},
            {149, 126, 112, 132, 124}
        }
    };

    private static Map<String, int[][]> symbolsFrequency = new HashMap<>();

    private static List<Map<String, Integer>> chunksFrequency = new ArrayList<>(5);

    private static List<List<String>> symbolsEmpiricalDistribution = new ArrayList<>(5);

    static {
        for (int i = 0; i < SYMBOLS.length; i++) {
            symbolsFrequency.put(SYMBOLS[i], RAW_SYMBOLS_FREQUENCY[i]);
        }

        for (int i = 0; i < 5; i++) {
            chunksFrequency.add(new HashMap<>());
        }

        for (String row : RAW_CHUNKS_FREQUENCY) {
            String[] data = row.split(",");
            
            for (int i = 0; i < 5; i++) {
                int index = i * 2; 

                String key = data[index];
                Integer value = Integer.parseInt(data[index + 1]);
                
                if(value == 0) {
                    continue;
                }

                chunksFrequency.get(i).put(key, value);
            }
        }
        
        for (int i = 0; i < 5; i++) {
            symbolsEmpiricalDistribution.add(new ArrayList<>());
        }

        for(int i = 0; i < 5; i++) {
            List<String> reel = symbolsEmpiricalDistribution.get(i);
            for(String symbol : SYMBOLS) {
                for(int j = 0; j < 3; j++) {
                    for(int n=0; n < symbolsFrequency.get(symbol)[j][i]; n++) {
                        reel.add(symbol);
                    }
                }
            }
        }
    }

    private static void random(List<Chromosome> population, int size) {
        population.clear();

        for (int i = 0; i < size; i++) {
            Chromosome chromosome = new Chromosome();

            for(int r = 0; r < chromosome.reels.size(); r++) {
                List<String> reel = chromosome.reels.get(r);
                List<String> distribution = symbolsEmpiricalDistribution.get(r);

                for(int s = 0; s < REEL_SIZE; s++) {
                    reel.add(distribution.get(PRNG.nextInt(distribution.size())));
                }
            }

            population.add(chromosome);
        }
    }

    private static void evaluate(Chromosome chromosome) {
        //TODO Estimate the RTP of the chromosome using Monte Carlo simulation.

        Map<String, int[][]> symbolsApproximatedFrequency = new HashMap<>();
        for (int s = 0; s < SYMBOLS.length; s++) {
            symbolsApproximatedFrequency.put(SYMBOLS[s], new int[3][5]);
        }

        List<Map<String, Integer>> chunksApproximatedFrequency = new ArrayList<>(5);
        for (int r = 0; r < 5; r++) {
            chunksApproximatedFrequency.add(new HashMap<>());
        }

        String[][] screen = {
            {"", "", "", "", ""},
            {"", "", "", "", ""},
            {"", "", "", "", ""},
        };

        for(int g=0; g < APPROXIMATION_SIZE; g++) {
            for(int r=0; r < 5; r++) {
                List<String> reel = chromosome.reels.get(r);
                int start = PRNG.nextInt(reel.size());

                for(int s=0; s < 3; s++) {
                    String symbol = screen[s][r] = reel.get((start + s) % reel.size());
                    symbolsApproximatedFrequency.get(symbol)[s][r]++;
                }

                String chunk = String.join("", screen[0][r], screen[1][r], screen[2][r]);
                chunksApproximatedFrequency.get(r).put(chunk, chunksApproximatedFrequency.get(r).getOrDefault(chunk, 0) + 1);
            }
        }

        /* Calculate fitness based on the difference between approximated and empirical frequencies. */
        double divisor = 1;
        chromosome.fitness = 0;
        chromosome.fitness += (100*chromosome.rtp - 100*RTP_TARGET) * (100*chromosome.rtp - 100*RTP_TARGET);

        for(String symbol : SYMBOLS) {
            int[][] empirical = symbolsFrequency.get(symbol);
            int[][] approximated = symbolsApproximatedFrequency.get(symbol);
            for(int r=0; r < 3; r++) {
                for(int c=0; c < 5; c++) {
                    chromosome.fitness += (approximated[r][c] - SAMPLES_RATIO*empirical[r][c]) * (approximated[r][c] - SAMPLES_RATIO*empirical[r][c]);
                    divisor += 1;
                }
            }
        }

        for(int r=0; r < 5; r++) {
            Map<String, Integer> empirical = chunksFrequency.get(r);
            Map<String, Integer> approximated = chunksApproximatedFrequency.get(r);

            Set<String> intersection = new HashSet<>(empirical.keySet());
            intersection.retainAll(approximated.keySet());

            for(String chunk : intersection) {
                int empiricalValue = empirical.get(chunk);
                int approximatedValue = approximated.get(chunk);
                chromosome.fitness += (approximatedValue - SAMPLES_RATIO*empiricalValue) * (approximatedValue - SAMPLES_RATIO*empiricalValue);
                divisor += 1;
            }

            Set<String> onlyInEmpirical = new HashSet<>(empirical.keySet());
            onlyInEmpirical.removeAll(intersection); 
            for(String chunk : onlyInEmpirical) {
                int empiricalValue = empirical.get(chunk);
                chromosome.fitness += (0 - SAMPLES_RATIO*empiricalValue) * (0 - SAMPLES_RATIO*empiricalValue);
                divisor += 1;
            }

            Set<String> onlyInApproximated = new HashSet<>(approximated.keySet());
            onlyInApproximated.removeAll(intersection);
            for(String chunk : onlyInApproximated) {
                int approximatedValue = approximated.get(chunk);
                chromosome.fitness += (approximatedValue - SAMPLES_RATIO*0) * (approximatedValue - SAMPLES_RATIO*0);
                divisor += 1;
            }
        }

        /* Root Mean Squared Error (RMSE) */
        chromosome.fitness = Math.sqrt(chromosome.fitness / divisor);
    }

    private static void evaluate(List<Chromosome> population) {
        for (Chromosome chromosome : population) {
            evaluate(chromosome);
        }
    }

    private static Chromosome selection(List<Chromosome> population) {
        int numberOfTrys = PRNG.nextInt(population.size());

        Chromosome result = population.get( PRNG.nextInt(population.size()) );

        for(int t=0; t < numberOfTrys; t++) {
            Chromosome candidate = population.get( PRNG.nextInt(population.size()) );

            if(candidate.fitness < result.fitness) {
                result = candidate;
            }
        }

        return result;
    }

    private static Chromosome crossover(Chromosome first, Chromosome second) {
        Chromosome offspring = new Chromosome();

        for(int r=0; r < 5; r++) {
            List<String> firstReel = first.reels.get(r);
            List<String> secondReel = second.reels.get(r);
            List<String> offspringReel = offspring.reels.get(r);

            int length = Math.min(firstReel.size(), secondReel.size());
            for(int o=0; o < length; o++) {
                if(PRNG.nextBoolean()) {
                    offspringReel.add(firstReel.get(o));
                } else {
                    offspringReel.add(secondReel.get(o));
                }
            }
        }

        return offspring;
    }

    private static void mutate(Chromosome offspring) {
        if(MUTATION_RATE <= PRNG.nextDouble()) {
            return;
        }

        List<String> reel = offspring.reels.get(PRNG.nextInt(5));
        switch(PRNG.nextInt(3)) {
            case 0:
                if(reel.size() > 0) reel.remove(PRNG.nextInt(reel.size()));
                break;
            case 1:
                reel.add(PRNG.nextInt(reel.size()), SYMBOLS[PRNG.nextInt(SYMBOLS.length)]);
                break;
            case 2:
                if(reel.size() > 0) reel.set(PRNG.nextInt(reel.size()), SYMBOLS[PRNG.nextInt(SYMBOLS.length)]);
                break;
        }
    }

    private static void trim(List<Chromosome> population, int size) {
        population.sort(Comparator.comparingDouble(c -> c.fitness));
        while(population.size() > size) {
            population.remove(population.size() - 1);
        }
    }

    private static void save(List<Chromosome> population, String filename) throws IOException {
        String json = (new GsonBuilder().setPrettyPrinting().create()).toJson(population);
        Files.writeString(Path.of(filename), json);
    }

    private static void load(List<Chromosome> population, String string) throws IOException {
        population.clear();
        String json = Files.readString(Path.of(string));
        population.addAll((new GsonBuilder().setPrettyPrinting().create()).fromJson(json, new TypeToken<List<Chromosome>>(){}.getType()));
    }

    public static void main(String[] args) throws IOException {
        List<Chromosome> population = new ArrayList<>();

        // random(population, POPULATION_SIZE);
        // evaluate(population);
        // save(population, "population.json");
        // System.exit(0);

        load(population, "population.json");

        for(int c=0; c<NUMBER_OF_OPTIMIZATION_INDIVIDUALS; c++) {
            Chromosome offspring = crossover(selection(population), selection(population));
            mutate(offspring);
            evaluate(offspring);
            population.add(offspring);
            System.out.println("Candidate: " + c + " of " + NUMBER_OF_OPTIMIZATION_INDIVIDUALS + ", Fitness: " + offspring.fitness + ", RTP: " + offspring.rtp);
        }

        trim(population, POPULATION_SIZE);
        save(population, "population.json");
    }
}
