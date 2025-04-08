package app.levels;
/**
 * Klasa odpowiedzialna za scenariusz trzeciego poziomu
 */
public class Level3 extends BaseLevel {

    public Level3(int avatarIndex, int level, int selectedSlot) {
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
