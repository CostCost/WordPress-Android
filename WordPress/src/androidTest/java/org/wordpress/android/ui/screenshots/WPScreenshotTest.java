package org.wordpress.android.ui.screenshots;

import android.test.suitebuilder.annotation.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.CardView;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wordpress.android.R;
import org.wordpress.android.datasets.ReaderTagTable;
import org.wordpress.android.models.ReaderTag;
import org.wordpress.android.models.ReaderTagList;
import org.wordpress.android.ui.WPLaunchActivity;
import org.wordpress.android.ui.posts.EditPostActivity;
import org.wordpress.android.ui.reader.views.ReaderSiteHeaderView;
import org.wordpress.android.util.image.ImageType;

import java.util.function.Supplier;

import static org.wordpress.android.BuildConfig.SCREENSHOT_LOGINPASSWORD;
import static org.wordpress.android.BuildConfig.SCREENSHOT_LOGINUSERNAME;
import static org.wordpress.android.ui.screenshots.support.WPScreenshotSupport.clickOn;
import static org.wordpress.android.ui.screenshots.support.WPScreenshotSupport.clickOnCellAtIndexIn;
import static org.wordpress.android.ui.screenshots.support.WPScreenshotSupport.getCurrentActivity;
import static org.wordpress.android.ui.screenshots.support.WPScreenshotSupport.moveCaretToEndAndDisplayIn;
import static org.wordpress.android.ui.screenshots.support.WPScreenshotSupport.hasElement;
import static org.wordpress.android.ui.screenshots.support.WPScreenshotSupport.populateTextField;
import static org.wordpress.android.ui.screenshots.support.WPScreenshotSupport.pressBackUntilElementIsVisible;
import static org.wordpress.android.ui.screenshots.support.WPScreenshotSupport.scrollToThenClickOn;
import static org.wordpress.android.ui.screenshots.support.WPScreenshotSupport.selectItemAtIndexInSpinner;
import static org.wordpress.android.ui.screenshots.support.WPScreenshotSupport.waitForAtLeastOneElementOfTypeToExist;
import static org.wordpress.android.ui.screenshots.support.WPScreenshotSupport.waitForAtLeastOneElementWithIdToExist;
import static org.wordpress.android.ui.screenshots.support.WPScreenshotSupport.waitForConditionToBeTrue;
import static org.wordpress.android.ui.screenshots.support.WPScreenshotSupport.waitForElementToBeDisplayed;
import static org.wordpress.android.ui.screenshots.support.WPScreenshotSupport.waitForElementToNotBeDisplayed;
import static org.wordpress.android.ui.screenshots.support.WPScreenshotSupport.waitForImagesOfTypeWithPlaceholder;
import static org.wordpress.android.ui.screenshots.support.WPScreenshotSupport.waitForRecyclerViewToStopReloading;

import tools.fastlane.screengrab.Screengrab;
import tools.fastlane.screengrab.UiAutomatorScreenshotStrategy;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class WPScreenshotTest {
    @ClassRule
    public static final WPLocaleTestRule LOCALE_TEST_RULE = new WPLocaleTestRule();


    @Rule
    public ActivityTestRule<WPLaunchActivity> mActivityTestRule = new ActivityTestRule<>(WPLaunchActivity.class,
            false, false);

    @Test
    public void wPScreenshotTest() {
        mActivityTestRule.launchActivity(null);
        Screengrab.setDefaultScreenshotStrategy(new UiAutomatorScreenshotStrategy());

        wPLogin();
        editBlogPost();
        navigateReader();
        navigateNotifications();
        navigateStats();
        wPLogout();
    }

    private void wPLogin() {
        // If we're already logged in, log out before starting
        if (!hasElement(R.id.login_button)) {
            wPLogout();
        }

        // Login Prologue – We want to log in, not sign up
        // See LoginPrologueFragment
        clickOn(R.id.login_button);

        // Email Address Screen – Fill it in and click "Next"
        // See LoginEmailFragment
        populateTextField(R.id.input, SCREENSHOT_LOGINUSERNAME);
        clickOn(R.id.primary_button);

        // Receive Magic Link or Enter Password Screen – Choose "Enter Password"
        // See LoginMagicLinkRequestFragment
        clickOn(R.id.login_enter_password);

        // Password Screen – Fill it in and click "Next"
        // See LoginEmailPasswordFragment
        populateTextField(R.id.input, SCREENSHOT_LOGINPASSWORD);
        clickOn(R.id.primary_button);

        // Login Confirmation Screen – Click "Continue"
        // See LoginEpilogueFragment
        clickOn(R.id.primary_button);
    }

    private void wPLogout() {
        // Click on the "Me" tab in the nav, then choose "Log Out"
        clickOn(R.id.nav_me);
        scrollToThenClickOn(R.id.row_logout);

        // Confirm that we want to log out
        clickOn(android.R.id.button1);
    }

    private void navigateReader() {
        // Choose the "Reader" tab in the nav
        clickOn(R.id.nav_reader);

        // Choose "Discover" from the spinner, but first, choose another item
        // to force a re-load – this avoids locale issues
        selectItemAtIndexInSpinner(getDiscoverTagIndex() == 0 ? 1 : 0, R.id.filter_spinner);
        selectItemAtIndexInSpinner(getDiscoverTagIndex(), R.id.filter_spinner);

        // Wait for the blog articles to load
        waitForAtLeastOneElementOfTypeToExist(ReaderSiteHeaderView.class);
        waitForAtLeastOneElementOfTypeToExist(CardView.class);
        waitForImagesOfTypeWithPlaceholder(R.id.image_featured, ImageType.PHOTO);
        waitForImagesOfTypeWithPlaceholder(R.id.image_avatar, ImageType.AVATAR);
        waitForImagesOfTypeWithPlaceholder(R.id.image_blavatar, ImageType.BLAVATAR);

        waitForRecyclerViewToStopReloading();

        Screengrab.screenshot("screenshot_2");
    }

    private void editBlogPost() {
        // Choose the "sites" tab in the nav
        clickOn(R.id.nav_sites);

        waitForImagesOfTypeWithPlaceholder(R.id.my_site_blavatar, ImageType.BLAVATAR);
        Screengrab.screenshot("screenshot_3");

        // Click on the "Blog Posts" row
        scrollToThenClickOn(R.id.row_blog_posts);

        // Wait for the blog posts to load, then edit the first post
        waitForRecyclerViewToStopReloading();
        waitForAtLeastOneElementOfTypeToExist(CardView.class);
        clickOnCellAtIndexIn(0, R.id.recycler_view);

        // Wait for the editor to appear and load all images
        waitForElementToBeDisplayed(R.id.aztec);
        waitForConditionToBeTrue(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return editPostActivityIsNoLongerLoadingImages();
            }
        });

        // Click in the post title editor and ensure the caret is at the end of the title editor
        scrollToThenClickOn(R.id.title);
        moveCaretToEndAndDisplayIn(R.id.title);

        Screengrab.screenshot("screenshot_1");

        // Exit back to the main activity
        pressBackUntilElementIsVisible(R.id.nav_sites);
    }

    private void navigateNotifications() {
        // Click on the "Notifications" tab in the nav
        clickOn(R.id.nav_notifications);

        waitForAtLeastOneElementWithIdToExist(R.id.note_content_container);
        waitForImagesOfTypeWithPlaceholder(R.id.note_avatar, ImageType.AVATAR);

        Screengrab.screenshot("screenshot_5");
    }

    private void navigateStats() {
        // Click on the "Sites" tab in the nav, then choose "Stats"
        clickOn(R.id.nav_sites);
        scrollToThenClickOn(R.id.row_stats);

        // If there's a pop-up message, dismiss it
        if (hasElement(R.id.promo_dialog_button_positive)) {
            clickOn(R.id.promo_dialog_button_positive);
        }

        // Select "Days" from the spinner
        selectItemAtIndexInSpinner(1, R.id.filter_spinner);

        // Wait for the stats to load
        waitForElementToNotBeDisplayed(R.id.stats_empty_module_placeholder);

        Screengrab.screenshot("screenshot_4");

        // Exit the Stats Activity
        pressBackUntilElementIsVisible(R.id.nav_sites);
    }

    private static int getDiscoverTagIndex() {
        ReaderTagList tagList = ReaderTagTable.getDefaultTags();
        for (int i = 0; i < tagList.size(); i++) {
            ReaderTag tag = tagList.get(i);
            if (tag.isDiscover()) {
                return i;
            }
        }
        return -1;
    }

    private boolean editPostActivityIsNoLongerLoadingImages() {
        EditPostActivity editPostActivity = (EditPostActivity) getCurrentActivity();
        return editPostActivity.getAztecImageLoader().getNumberOfImagesBeingDownloaded() == 0;
    }
}