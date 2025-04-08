package app.levels;
/**
 * Klasa odpowiedzialna za scenariusz drugiego poziomu
 */
public class Level2 extends BaseLevel {

    public Level2(int avatarIndex, int level, int selectedSlot) {
        super(avatarIndex, level, selectedSlot);

        this.backgroundSpeed = 7.0f;//predkosc tla
        this.obstacleSpeed = 7.0f; //predkosc przeszkod jest taka sama jak diamentow
        this.NUM_OBSTACLES = 10;
        this.levelLength = screenWidth * 6;
    }

    public void update() {
        super.update(); //wywolanie logiki BaseLevel
    }

}
