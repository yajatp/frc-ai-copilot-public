package org.mercsmavs.frccopilot.dashboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * {@code --open} is the first thing a new user touches and the last thing anyone tests, because
 * each branch only runs on its own platform. Asserting the command strings directly gives the
 * Windows and Linux forms coverage from whichever machine happens to run the suite.
 */
class BrowserCommandTest {

    private static final String URL = "http://localhost:5800";

    @Test
    void macOsUsesOpen() {
        assertEquals(List.of("open", URL), DashboardMain.browserCommand("Mac OS X", URL));
    }

    @Test
    void linuxUsesXdgOpen() {
        assertEquals(List.of("xdg-open", URL), DashboardMain.browserCommand("Linux", URL));
    }

    @Test
    void windowsPassesAStartTitleSoTheUrlIsNotConsumedAsOne() {
        List<String> command = DashboardMain.browserCommand("Windows 11", URL);

        assertEquals("cmd", command.get(0));
        assertEquals("/c", command.get(1));
        assertEquals("start", command.get(2));
        assertEquals(URL, command.get(command.size() - 1), "the URL must be the final argument");

        // The argument between `start` and the URL is the window title. It must be non-empty:
        // whether Java quotes an empty argument on Windows is a JDK implementation detail, and an
        // unquoted empty argument collapses, leaving `start` to treat the URL as the title and open
        // nothing at all.
        String title = command.get(3);
        assertFalse(title.isEmpty(), "start's title argument must not be empty");
        assertEquals(5, command.size(), "expected: cmd /c start <title> <url>");
    }

    @Test
    void osNameMatchingIsCaseInsensitive() {
        assertEquals(
                DashboardMain.browserCommand("Windows 11", URL),
                DashboardMain.browserCommand("WINDOWS 11", URL));
        assertEquals(
                DashboardMain.browserCommand("Mac OS X", URL),
                DashboardMain.browserCommand("mac os x", URL));
    }

    @Test
    void anUnrecognizedOsFallsBackToTheFreedesktopOpener() {
        // Better a command that may not exist than no command at all: the failure is caught and
        // reported alongside the URL, which is the same outcome as any other opener failing.
        assertTrue(DashboardMain.browserCommand("SunOS", URL).contains("xdg-open"));
    }
}
