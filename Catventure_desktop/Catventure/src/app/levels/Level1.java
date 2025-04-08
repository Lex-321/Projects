package app.levels;

/**
 * Klasa odpowiedzialna za scenariusz pierwszego poziomu
 */
public class Level1 extends BaseLevel {

    public Level1(int avatarIndex, int level, int selectedSlot) {
        super(avatarIndex, level, selectedSlot);

        this.backgroundSpeed = 5.0f;//predkosc tla
        this.obstacleSpeed = 5.0f; //predkosc przeszkod jest taka sama jak diamentow
        this.NUM_OBSTACLES = 5;
//        this.levelLength = screenWidth * 8;
    }

    public void update() {
        super.update(); //wywolanie logiki BaseLevel
    }

}
