package com.example.catventure;

import android.content.Context;
import android.media.MediaPlayer;
import com.example.catventure.levels.Level_3;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class Level_3Test {
    private Level_3 level3;
    private Context context;
    @Mock
    private MediaPlayer mockMediaPlayer;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        context = RuntimeEnvironment.getApplication();
        mockMediaPlayer = mock(MediaPlayer.class);
        when(MediaPlayer.create(Mockito.any(Context.class), Mockito.anyInt())).thenReturn(mockMediaPlayer);
        level3 = new Level_3(context, 0, 1, 1);
    }
    @Test
    public void testCustomInitialization() {
        level3 = new Level_3(context, 2, 3, 4);
        assertEquals(10.0f, level3.backgroundSpeed, 0.001);
        assertEquals(10.0f, level3.obstacleSpeed, 0.001);
        assertEquals(5, level3.NUM_OBSTACLES);
    }
}
