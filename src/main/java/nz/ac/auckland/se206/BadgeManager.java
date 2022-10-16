package nz.ac.auckland.se206;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import nz.ac.auckland.se206.CategorySelect.Difficulty;
import nz.ac.auckland.se206.daos.GameSettingDao;
import nz.ac.auckland.se206.daos.UserDaoJson;
import nz.ac.auckland.se206.daos.UserStatsDao;
import nz.ac.auckland.se206.models.BadgeModel;
import nz.ac.auckland.se206.models.GameModel;
import nz.ac.auckland.se206.models.GameSettingModel;
import nz.ac.auckland.se206.models.UserModel;

public class BadgeManager {

  private static List<BadgeModel> availBadges;
  private static UserModel user;

  // hash maps of grouped badges
  private static LinkedHashMap<Integer, BadgeModel> timeThreshold = new LinkedHashMap<>();
  private static HashMap<String, BadgeModel> settingsBadges = new HashMap<>();
  private static HashMap<Difficulty, Integer> wordsCount = new HashMap<>();
  private static LinkedHashMap<Difficulty, BadgeModel> wordsBadges = new LinkedHashMap<>();
  private static LinkedHashMap<Integer, BadgeModel> gameCountThreshold = new LinkedHashMap<>();

  /**
   * Initialize the array of existing badges and related hashmaps. Current badge count: 20 badges
   */
  public static void initializeBadges() {

    BadgeModel badge;
    availBadges = new ArrayList<>();
    // create play first game badge
    badge =
        new BadgeModel(
            0,
            "Play First Game!",
            "You have played your first Quick Draw game! Welcome to the party!",
            "");
    availBadges.add(badge);
    // create get first win and lose game badges
    badge = new BadgeModel(1, "Win a Game!", "Congrats on your first win! Let's Keep Going!", "");
    availBadges.add(badge);
    badge = new BadgeModel(2, "Lose a Game!", "Tough luck! Try again, and you'll get it!", "");
    availBadges.add(badge);

    // create other badges
    initializeBadgesTime();
    initializeBadgesSettings();
    initializeBadgesWords();
    initializeBadgesCount();
  }

  /**
   * Checks whether a given completed game will result in a new badge for a given user, and gives
   * said badge to user. Checks every badge.
   *
   * @param username of the user to check the badges of
   * @param game that may trigger a new badge
   * @param actualDifficulty
   * @return the number of new badges that was given
   */
  public static int checkNewBadges(String username, GameModel game, Difficulty actualDifficulty) {
    // initialise return
    int newBadgeCount = 0;
    // get necessary info
    UserDaoJson userDao = new UserDaoJson();
    user = userDao.get(username);
    boolean won = game.getWon();

    // check for first game
    if (!(userDao.checkBadgeExists(availBadges.get(0), user))) {
      userDao.addBadge(availBadges.get(0), username);
      newBadgeCount++;
    }

    // check for first win badge
    if (won && !(userDao.checkBadgeExists(availBadges.get(1), user))) {
      userDao.addBadge(availBadges.get(1), username);
      newBadgeCount++;
    }

    // check for first lose badge
    if (!(won) && !(userDao.checkBadgeExists(availBadges.get(2), user))) {
      userDao.addBadge(availBadges.get(2), username);
      newBadgeCount++;
    }

    // check grouped badges needing wins
    if (won) {
      newBadgeCount += checkNewBadgesTime(game.getTime()); // 3-6
      newBadgeCount += checkNewBadgesSettings(); // 7-10
    }

    // check grouped badges not needing a win
    newBadgeCount += checkNewBadgesWords(actualDifficulty);
    newBadgeCount += checkNewBadgesCount();

    return newBadgeCount;
  }

  /**
   * Returns all of the initialized badges in the game, or a null object if nothing has been
   * initialized.
   *
   * @return a list representation of badges
   */
  public static List<BadgeModel> getAllBadges() {
    return availBadges;
  }

  /**
   * Initializes all time-related badge array entries and hashmap. Current Badge count: 4. Badge ID
   * range: 3 to 6
   */
  private static void initializeBadgesTime() {
    // create time based badges and populate hashmap
    // hash map: key = time threshold, value = relevant badge
    BadgeModel badge;
    // #3: under 30 seconds
    badge = new BadgeModel(3, "Fast!", "You finished a game under 30 seconds!", "");
    availBadges.add(badge);
    timeThreshold.put(30, badge);
    // #4: under 15 seconds
    badge = new BadgeModel(4, "Speedy!", "You finished a game under 15 seconds!", "");
    availBadges.add(badge);
    timeThreshold.put(15, badge);
    // #5: under 10 seconds
    badge = new BadgeModel(5, "Turbo!", "You finished a game under 10 seconds!", "");
    availBadges.add(badge);
    timeThreshold.put(10, badge);
    // #6: under 5 seconds
    badge =
        new BadgeModel(6, "Lightning!", "You finished a game under 5 seconds! You're cracked!", "");
    availBadges.add(badge);
    timeThreshold.put(5, badge);
  }

  /**
   * Initializes all settings-related badge array entries and hashmap. Current Badge count: 4. Badge
   * ID range: 7 to 10
   */
  private static void initializeBadgesSettings() {
    // create difficulty based badges and populate hashmap
    // hashmap: key = setting difficulty, value = relevant badge
    BadgeModel badge;
    // #7: all easy settings
    badge = new BadgeModel(7, "Easy-Peasy!", "You won a game with all 'easy' settings!", "");
    availBadges.add(badge);
    settingsBadges.put("EASY", badge);
    // #8: all medium settings
    badge = new BadgeModel(8, "Medium Rare!", "You won a game with all 'medium' settings!", "");
    availBadges.add(badge);
    settingsBadges.put("MEDIUM", badge);
    // #9: all hard settings
    badge = new BadgeModel(9, "Hardcore!", "You won a game with all 'hard' settings!", "");
    availBadges.add(badge);
    settingsBadges.put("HARD", badge);
    // #10: hard accuracy + all others master
    badge =
        new BadgeModel(
            10,
            "Master-mind!",
            "You won a game with the hardest possible settings! You're a master!",
            "");
    availBadges.add(badge);
    settingsBadges.put("MASTER", badge);
  }

  /**
   * Initializes all word-related badge array entries and hashmap. Current Badge count: 4. Badge ID
   * range: 11 to 14
   */
  private static void initializeBadgesWords() {
    // create word based badges and add to hashmap
    // hash map: key = actual/word difficulty, value = relevant badge
    BadgeModel badge;
    // #11: all words in easy played
    badge = new BadgeModel(11, "Easy Ace!", "You've played all of the words on easy!", "");
    availBadges.add(badge);
    wordsBadges.put(Difficulty.EASY, badge);
    // #12: all words in medium played
    badge = new BadgeModel(12, "Medium Maestro!", "You've played all of the words on medium!", "");
    availBadges.add(badge);
    wordsBadges.put(Difficulty.MEDIUM, badge);
    // #13: all words in hard played
    badge = new BadgeModel(13, "Hard Professional!", "You've played all of the words on hard!", "");
    availBadges.add(badge);
    wordsBadges.put(Difficulty.HARD, badge);
    // #14: all words overall played
    badge =
        new BadgeModel(
            14,
            "Word Wizard!",
            "You've played all of the words in the game! Did you learn some new words?",
            "");
    availBadges.add(badge);
    wordsBadges.put(Difficulty.MASTER, badge);

    // populate second hashmap: key = difficulty, value = number of words in that
    // category
    wordsCount.put(Difficulty.EASY, 144);
    wordsCount.put(Difficulty.MEDIUM, 132);
    wordsCount.put(Difficulty.HARD, 69);
  }

  /**
   * Initializes all number of games related badge array entries. Current Badge count: 5. Badge ID
   * range: 15 to 19
   */
  private static void initializeBadgesCount() {
    // create game count badges and add to hashmap
    // hashmap: key = number of games threshold, value = relevant badge
    BadgeModel badge;
    // #15: play 5 games overall
    badge = new BadgeModel(15, "Newbie!", "You've played 5 games!", "");
    availBadges.add(badge);
    gameCountThreshold.put(5, badge);
    // #16: play 10 games overall
    badge = new BadgeModel(16, "Amateur!", "You've played 10 games!", "");
    availBadges.add(badge);
    gameCountThreshold.put(10, badge);
    // #17: play 25 games overall
    badge = new BadgeModel(17, "Competent!", "You've played 25 games!", "");
    availBadges.add(badge);
    gameCountThreshold.put(25, badge);
    // #18: play 50 games overall
    badge = new BadgeModel(18, "Proficient!", "You've played 50 games!", "");
    availBadges.add(badge);
    gameCountThreshold.put(50, badge);
    // #19: play 100 games overall
    badge = new BadgeModel(19, "Expert!", "You've played 100 games! That's a lot!", "");
    availBadges.add(badge);
    gameCountThreshold.put(100, badge);
  }

  /**
   * Checks whether the user is eligible for a new time-based badge where badge id ranges from 3 to
   * 6 inclusive
   *
   * @param userTime in seconds taken to complete drawing
   * @return number of new badges awarded
   */
  private static int checkNewBadgesTime(int userTime) {

    // initialise necessary helper variables
    int newBadgeCount = 0;
    UserDaoJson userDao = new UserDaoJson();

    // check from slowest to fastest time if the user is eligible for a new badge
    for (int time : timeThreshold.keySet()) {
      // check requirements
      BadgeModel badge = timeThreshold.get(time);
      Boolean hasBadgeAlready = userDao.checkBadgeExists(badge, user);
      Boolean isWithinTime = (userTime <= time);
      if (!(hasBadgeAlready) && isWithinTime) {
        // add to database and set the boolean
        userDao.addBadge(badge, user.getUsername());
        newBadgeCount++;
      }
    }
    return newBadgeCount;
  }

  /**
   * Checks whether the user is eligible for a new settings-based badge where badge id ranges from 7
   * to 10 inclusive
   *
   * @return number of new badges awarded
   */
  private static int checkNewBadgesSettings() {
    // initialise necessary helper variables
    UserDaoJson userDao = new UserDaoJson();
    GameSettingDao settingDao = new GameSettingDao();
    GameSettingModel userSettings = settingDao.get(user.getId());

    // get difficulties
    String words = userSettings.getWords();
    String accuracy = userSettings.getAccuracy();
    String time = userSettings.getTime();
    String confidence = userSettings.getConfidence();

    // check if all the settings are the same
    Boolean isTheSameGeneral =
        words.equals(accuracy) && words.equals(time) && words.equals(confidence);
    Boolean isTheSameMaster =
        accuracy.equals("HARD")
            && words.equals("MASTER")
            && words.equals(time)
            && words.equals(confidence);

    // give badge if necessary
    if (isTheSameGeneral || isTheSameMaster) {
      // identify badge and whether the user already has it
      BadgeModel badge = settingsBadges.get(words);
      if (!userDao.checkBadgeExists(badge, user)) {
        userDao.addBadge(badge, user.getUsername());
        return 1;
      }
    }
    return 0;
  }

  /**
   * Checks whether the user is eligible for a new settings-based badge where badge id ranges from
   * 11 to 14 inclusive
   *
   * @param actualDifficulty of the word in the last game played
   * @return number of new badges awarded
   */
  private static int checkNewBadgesWords(Difficulty actualDifficulty) {
    int newBadgeCount = 0;
    // get history of words
    UserStatsDao userStatsDao = new UserStatsDao();
    UserDaoJson userDao = new UserDaoJson();
    List<String> wordHistory = userStatsDao.getHistoryMap(user.getId()).get(actualDifficulty);

    // get relevant numbers
    BadgeModel badge = wordsBadges.get(actualDifficulty);
    int categorySize = wordsCount.get(actualDifficulty);
    int historySize = wordHistory.size();

    // calculate if the user has played all of the words at least once AND they
    // don't already have the badge
    if ((historySize == categorySize)) {
      userDao.addBadge(badge, user.getUsername());
      newBadgeCount++;

      // calculate how much word badges they have
      int wordBadgeCount = 0;
      for (Difficulty diff : wordsBadges.keySet()) {
        if (userDao.checkBadgeExists(wordsBadges.get(diff), user)) {
          wordBadgeCount++;
        }
      }
      // check if they have exactly two word badges (the two previous, plus the one we
      // just received)
      if ((wordBadgeCount == 2)) {
        userDao.addBadge(availBadges.get(14), user.getUsername());
        newBadgeCount++;
      }
    }

    return newBadgeCount;
  }

  /**
   * Checks whether the user is eligible for a new number of games playedbased badge where badge id
   * ranges from 15 to 19 inclusive
   *
   * @return number of new badges awarded
   */
  private static int checkNewBadgesCount() {
    UserDaoJson userDao = new UserDaoJson();

    // get number of games played
    UserStatsDao userStatsDao = new UserStatsDao();
    int gameCount = userStatsDao.countGames(user.getId());

    // calculate which badges can be received
    for (int countThreshold : gameCountThreshold.keySet()) {
      if ((gameCount == countThreshold)) {
        BadgeModel badge = gameCountThreshold.get(countThreshold);
        userDao.addBadge(badge, user.getUsername());
        return 1;
      }
    }

    return 0;
  }
}
