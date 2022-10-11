package nz.ac.auckland.se206;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import nz.ac.auckland.se206.daos.GameSettingDao;
import nz.ac.auckland.se206.daos.UserDaoJson;
import nz.ac.auckland.se206.models.BadgeModel;
import nz.ac.auckland.se206.models.GameModel;
import nz.ac.auckland.se206.models.GameSettingModel;
import nz.ac.auckland.se206.models.UserModel;

public class BadgeManager {

  private static List<BadgeModel> availBadges;
  private static UserModel user;

  // hash maps of grouped badges
  private static HashMap<Integer, Integer> timeThreshold = new HashMap<>();
  private static HashMap<String, Integer> settingsIds = new HashMap<>();

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
    badge = new BadgeModel(1, "Win a Game!", "Congrats on your first win!", "");
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
   * Initializes all time-related badge array entries and hashmap. Current Badge count: 4. Badge ID
   * range: 3 to 6
   */
  private static void initializeBadgesTime() {
    BadgeModel badge;
    // create time based badges
    badge = new BadgeModel(3, "Fast!", "You finished a game under 30 seconds!", "");
    availBadges.add(badge);
    badge = new BadgeModel(4, "Speedy!", "You finished a game under 15 seconds!", "");
    availBadges.add(badge);
    badge = new BadgeModel(5, "Turbo!", "You finished a game under 10 seconds!", "");
    availBadges.add(badge);
    badge =
        new BadgeModel(6, "Lightning!", "You finished a game under 5 seconds! You're cracked!", "");
    availBadges.add(badge);
    // create maps in hash map for time thresholds
    timeThreshold.put(3, 30);
    timeThreshold.put(4, 15);
    timeThreshold.put(5, 10);
    timeThreshold.put(6, 5);
  }

  /**
   * Initializes all settings-related badge array entries and hashmap. Current Badge count: 4. Badge
   * ID range: 7 to 10
   */
  private static void initializeBadgesSettings() {
    BadgeModel badge;
    // create difficulty based badges
    badge = new BadgeModel(7, "Easy-Peasy!", "You won a game with all 'easy' settings!", "");
    availBadges.add(badge);
    badge = new BadgeModel(8, "Medium Rare!", "You won a game with all 'medium' settings!", "");
    availBadges.add(badge);
    badge = new BadgeModel(9, "Hardcore!", "You won a game with all 'hard' settings!", "");
    availBadges.add(badge);
    badge =
        new BadgeModel(
            10,
            "Quick Draw Master!",
            "You won a game with the hardest possible settings! You're a master!",
            "");
    availBadges.add(badge);

    settingsIds.put("EASY", 7);
    settingsIds.put("MEDIUM", 8);
    settingsIds.put("HARD", 9);
    settingsIds.put("MASTER", 10);
  }

  /**
   * Initializes all word-related badge array entries. Current Badge count: 4. Badge ID range: 11 to
   * 14
   */
  private static void initializeBadgesWords() {
    BadgeModel badge;
    // create word based badges
    badge = new BadgeModel(11, "Easy Ace!", "You've played all of the words on easy!", "");
    availBadges.add(badge);
    badge = new BadgeModel(12, "Medium Maestro!", "You've played all of the words on medium!", "");
    availBadges.add(badge);
    badge = new BadgeModel(13, "Hard Professional!", "You've played all of the words on hard!", "");
    availBadges.add(badge);
    badge =
        new BadgeModel(
            14,
            "Word Wizard!",
            "You've played all of the words in the game! Did you learn some new words?",
            "");
    availBadges.add(badge);
  }

  /**
   * Initializes all time-related badge array entries. Current Badge count: 5. Badge ID range: 15 to
   * 19
   */
  private static void initializeBadgesCount() {
    BadgeModel badge;
    // create game count badges
    badge = new BadgeModel(15, "Newbie!", "You've played 5 games!", "");
    availBadges.add(badge);
    badge = new BadgeModel(16, "Amateur!", "You've played 10 games!", "");
    availBadges.add(badge);
    badge = new BadgeModel(17, "Competent!", "You've played 25 games!", "");
    availBadges.add(badge);
    badge = new BadgeModel(18, "Proficient!", "You've played 50 games!", "");
    availBadges.add(badge);
    badge = new BadgeModel(19, "Expert!", "You've played 100 games! That's a lot!", "");
    availBadges.add(badge);
  }

  /**
   * Checks whether a given completed game will result in a new badge for a given user, and gives
   * said badge to user. Checks every badge.
   *
   * @param username of the user to check the badges of
   * @param game that may trigger a new badge
   * @return the number of new badges that was given
   */
  public static int checkNewBadges(String username, GameModel game) {
    // initialise return
    int newBadgeCount = 0;
    // get necessary info
    UserDaoJson userDao = new UserDaoJson();
    user = userDao.get(username);
    boolean won = game.getWon();

    // check for first game
    if (!(userDao.checkExists(availBadges.get(0), user))) {
      userDao.addBadge(availBadges.get(0), username);
      newBadgeCount++;
    }

    // check for first win badge
    if (won && !(userDao.checkExists(availBadges.get(1), user))) {
      userDao.addBadge(availBadges.get(1), username);
      newBadgeCount++;
    }

    // check for first lose badge
    if (!(won) && !(userDao.checkExists(availBadges.get(2), user))) {
      userDao.addBadge(availBadges.get(2), username);
      newBadgeCount++;
    }

    // check grouped badges needing wins
    if (won) {
      newBadgeCount += checkNewBadgesTime(game.getTime()); // 3-6
      newBadgeCount += checkNewBadgesSettings(user.getId()); // 7-10
    }

    return newBadgeCount;
  }

  /**
   * Checks whether the user is eligible for a new time-based badge where badge id ranges from 3 to
   * 6 inclusive
   *
   * @param time in seconds taken to complete drawing
   * @return number of new badges awarded
   */
  private static int checkNewBadgesTime(int time) {

    // initialise necessary helper variables
    int newBadgeCount = 0;
    UserDaoJson userDao = new UserDaoJson();

    // check from slowest to fastest time if the user is eligible for a new badge
    for (int i = 3; i < 7; i++) {
      // check requirements
      BadgeModel badge = availBadges.get(i);
      Boolean hasBadgeAlready = userDao.checkExists(badge, user);
      Boolean isWithinTime = (time <= timeThreshold.get(i));
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
   * to 10 6 inclusive
   *
   * @param id of the user's account
   * @return number of new badges awarded
   */
  private static int checkNewBadgesSettings(String id) {
    // initialise necessary helper variables
    UserDaoJson userDao = new UserDaoJson();
    GameSettingDao settingDao = new GameSettingDao();
    GameSettingModel userSettings = settingDao.get(id);

    // check if all the settings are the same
    String words = userSettings.getWords();
    String accuracy = userSettings.getAccuracy();
    String time = userSettings.getTime();
    String confidence = userSettings.getConfidence();

    Boolean isTheSameGeneral =
        words.equals(accuracy) && words.equals(time) && words.equals(confidence);
    Boolean isTheSameMaster =
        accuracy.equals("HARD")
            && words.equals("MASTER")
            && words.equals(time)
            && words.equals(confidence);

    System.out.println(words);
    System.out.println(time);
    System.out.println(isTheSameGeneral.toString());
    System.out.println(isTheSameMaster.toString());

    // give badge if necessary
    if (isTheSameGeneral || isTheSameMaster) {
      // identify badge and whether the user already has it
      BadgeModel badge = availBadges.get(settingsIds.get(words));
      if (!userDao.checkExists(badge, user)) {
        userDao.addBadge(badge, user.getUsername());
        return 1;
      }
    }

    return 0;
  }
}
