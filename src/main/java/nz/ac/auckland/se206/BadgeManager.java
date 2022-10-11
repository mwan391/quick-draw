package nz.ac.auckland.se206;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import nz.ac.auckland.se206.daos.UserDaoJson;
import nz.ac.auckland.se206.models.BadgeModel;
import nz.ac.auckland.se206.models.GameModel;
import nz.ac.auckland.se206.models.UserModel;

public class BadgeManager {

  private static List<BadgeModel> availBadges;
  private static UserModel user;

  // hash maps of grouped badges
  private static HashMap<Integer, Integer> timeThreshold = new HashMap<>();

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
    initializeBadgesDifficulty();
    initializeBadgesWords();
    initializeBadgesCount();
  }

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

  private static void initializeBadgesDifficulty() {
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
  }

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

  public static boolean checkNewBadges(String username, GameModel game) {
    // initialise return
    boolean hasNewBadge = false;
    // get necessary info
    UserDaoJson userDao = new UserDaoJson();
    user = userDao.get(username);
    boolean won = game.getWon();

    // check for first game
    if (!(userDao.checkExists(availBadges.get(0), user))) {
      userDao.addBadge(availBadges.get(0), username);
      hasNewBadge = true;
    }

    // check for first win badge
    if (won && !(userDao.checkExists(availBadges.get(1), user))) {
      userDao.addBadge(availBadges.get(1), username);
      hasNewBadge = true;
    }

    // check for first lose badge
    if (!(won) && !(userDao.checkExists(availBadges.get(2), user))) {
      userDao.addBadge(availBadges.get(2), username);
      hasNewBadge = true;
    }

    // check grouped badges needing wins
    if (won) {
      hasNewBadge |= checkNewBadgesTime(game.getTime()); // 3-6
    }

    return hasNewBadge;
  }

  /**
   * Checks whether the user is eligible for a new time-based badge where badge id ranges from 3 to
   * 6 inclusive
   *
   * @param time in seconds taken to complete drawing
   * @return true if a new badge was found
   */
  private static boolean checkNewBadgesTime(int time) {

    // initialise necessary helper variables
    boolean hasNewBadge = false;
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
        hasNewBadge = true;
      }
    }
    return hasNewBadge;
  }
}
