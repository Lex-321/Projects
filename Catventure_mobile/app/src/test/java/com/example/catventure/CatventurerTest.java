package com.example.catventure;
import android.graphics.Bitmap;
import android.graphics.Rect;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.example.catventure.levels.Obstacles;

public class CatventurerTest {

    private Catventurer catventurer;
    private Bitmap mockRunBitmap;
    private Bitmap mockJumpBitmap;
    private Obstacles mockObstacle;

    @Before
    public void setUp() {
        // Tworzymy mocki bitmap dla animacji
        mockRunBitmap = mock(Bitmap.class);
        mockJumpBitmap = mock(Bitmap.class);

        // Tworzymy instancję Catventurer z mockowanymi bitmapami
        int[] runResources = {1, 2};  // Dummy values
        int[] jumpResources = {3, 4, 5};  // Dummy values
        catventurer = new Catventurer(null, runResources, jumpResources, 0);

        // Mockowanie przeszkody
        mockObstacle = mock(Obstacles.class);
        Rect obstacleRect = new Rect(100, 100, 200, 200);  // Dummy values
        when(mockObstacle.getBoundingBox()).thenReturn(obstacleRect);
    }

    @Test
    public void testJump() {
        catventurer.jump();
        assertTrue(catventurer.isJumping());
        assertEquals(1, catventurer.getBoundingBox().top);
        catventurer.jump();
        assertTrue(catventurer.isJumping());
    }
    @Test
    public void testUpdate() {
        catventurer.jump();
        catventurer.update();
        assertTrue(catventurer.getBoundingBox().top < Catventurer.GROUND_LEVEL);
        catventurer.update();
        assertEquals(Catventurer.GROUND_LEVEL, catventurer.getBoundingBox().top);
    }
    @Test
    public void testGetBoundingBox() {
        Rect boundingBox = catventurer.getBoundingBox();
        assertNotNull(boundingBox);
    }
    @Test
    public void testCollidesWith() {
        catventurer.jump();
        assertFalse(catventurer.collidesWith(mockObstacle));
        catventurer.update();
        when(mockObstacle.getBoundingBox()).thenReturn(new Rect(0, 0, 100, 100));
        assertTrue(catventurer.collidesWith(mockObstacle));
    }
}
