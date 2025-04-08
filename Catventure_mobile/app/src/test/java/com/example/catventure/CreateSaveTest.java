package com.example.catventure;

import static org.junit.Assert.assertEquals;
import android.app.Application;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE,
        application = Application.class)
public class CreateSaveTest {
    private CreateSave createSave;
    private final int[] avatars = {0, 1, 2};
    @Before
    public void setUp() {
        createSave = Robolectric.buildActivity(CreateSave.class)
                .create()
                .get();
        createSave.currentAvatarIndex = 1;
    }
    @Test
    public void testShowPreviousAvatar() {
        createSave.showPreviousAvatar();
        assertEquals(0, createSave.currentAvatarIndex);
    }
    @Test
    public void testShowNextAvatar() {
        createSave.showNextAvatar();
        assertEquals(2, createSave.currentAvatarIndex);
    }
}



