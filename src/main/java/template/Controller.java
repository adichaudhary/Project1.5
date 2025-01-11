package template;

import java.util.ArrayList;
import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.util.Duration;

public class Controller {
	@FXML
	private GridPane root;
	
	@FXML
	private Button startButton, helpButton, exitButton;
	
	@FXML
	private Pane panel, bg, place;
	
	@FXML
	private Label points;
	
	@FXML
	private Label timer = new Label(Integer.toString(failTime));
	
	@FXML
	private ImageView charView = new ImageView();
	
	@FXML
	private ImageView enemyView = new ImageView();
	
	@FXML
	private ImageView moneyView = new ImageView();
	
	@FXML
	private PauseTransition delay;

	private int x = 0;
	private int y = 0;
	private int level = 0;
	private static int moneyCount = 0;
	private ArrayList<Integer> moneyXs = new ArrayList<>();
	private ArrayList<Integer> moneyYs = new ArrayList<>();
	private static int failTime = 30;
	private static int ogTime = 30;
	private static int factor = 25;
	private Timeline timeline, movement, collision;
	
	@FXML
	void initialize() {
		startMenu();
	}
	
	@FXML
	void start() {
		panel.setVisible(false);
		if (level != 1) {
			spawnZombie(3, 3);
			enemyPathfinding();
		}
		StackPane scoreStack = new StackPane();
        Rectangle blackRectangle = new Rectangle(50, 50);
        Label scoreLabel = new Label("SCORE");
        scoreLabel.setFont(Font.font("CaskaydiaCove NF", FontWeight.SEMI_BOLD, 12));
        scoreLabel.setTextFill(Color.WHITE);
        timer = new Label(failTime + "s");
        timer.setFont(Font.font("CaskaydiaCove NF", FontWeight.SEMI_BOLD, 12));
        timer.setTextFill(Color.WHITE);
        points = new Label(String.format("%03d", moneyCount));
        points.setFont(Font.font("CaskaydiaCove NF", FontWeight.SEMI_BOLD, 12));
        points.setTextFill(Color.WHITE);
        scoreStack.getChildren().addAll(blackRectangle, scoreLabel, points, timer);
        StackPane.setAlignment(scoreLabel, Pos.TOP_CENTER);
        StackPane.setAlignment(points, Pos.CENTER);
        StackPane.setAlignment(timer, Pos.BOTTOM_CENTER);
        root.add(scoreStack, 8, 0);
		spawn("down", 0, 0);
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				int z = i;
				int w = j;
				Random rand = new Random();
				int val = rand.nextInt(factor) + 1; 
				if (val == 1) {
					spawnMoney(z, w);
					moneyXs.add(z);
					moneyYs.add(w);
				}
			}
		}
	}
	
	@FXML
	void moveUp() {
		root.getChildren().remove(charView);
		y--;
		if(y < 0){
            y = 0;
        }
		if (y > 8) {
			y = 8;
		}
		spawn("up", x, y);
	}
	
	@FXML
	void moveDown() {
		root.getChildren().remove(charView);
		y++;
		if(y < 0){
            y = 0;
        }
		else if (y > 8) {
			y = 8;
		}
		spawn("down", x, y);
	}
	
	@FXML
	void moveLeft() {
		root.getChildren().remove(charView);
		x--;
		if(x < 0){
            x = 0;
        }
		else if (x > 8) {
			x = 8;
		}
		spawn("left", x, y);
	}
	
	@FXML
	void moveRight() {
		root.getChildren().remove(charView);
		x++;
		if(x < 0){
            x = 0;
        }
		else if (x > 8) {
			x = 8;
		}
		spawn("right", x, y);
	}
	
	@FXML
	void spawn(String name, int x, int y) {
		charView.setImage(new Image(getClass().getResourceAsStream(name + ".png")));
		charView.setFitWidth(50);
		if (name.equals("right") || name.equals("left")) {
			charView.setFitWidth(70);
		}
		charView.setPreserveRatio(true);
		root.getChildren().remove((int) charView.getX(), (int) charView.getY());
		root.add(charView, x, y);
		for (int i = 0; i < moneyXs.size(); i++) {
	        int moneyX = moneyXs.get(i);
	        int moneyY = moneyYs.get(i);
	        if (moneyX == x && moneyY == y) {
	            moneyCount += 100;
	            points.setText(Integer.toString(moneyCount));
	            moneyXs.remove(i);
	            moneyYs.remove(i);
	            removeAlg(charView, enemyView, moneyX, moneyY);
	        }
	    }
	}
	
	@FXML
	void spawnZombie(int x1, int y1) {
		enemyView.setImage(new Image(getClass().getResourceAsStream("zombie.png")));
		enemyView.setFitWidth(80);
		enemyView.setPreserveRatio(true);
		root.add(enemyView, x1, y1);
		removeAlg(charView, enemyView, x1, y1);
	}
	
	@FXML
	void spawnMoney(int moneyX, int moneyY) {
		ImageView moneyView = new ImageView();
		moneyView.setImage(new Image(getClass().getResourceAsStream("money.png")));
		moneyView.setFitWidth(50);
		moneyView.setPreserveRatio(true);
		root.add(moneyView, moneyX, moneyY);
	}
	
	@FXML
	void restartWorld() {
		level++;
	    root.getChildren().clear();
	    x = 0;
	    y = 0;
	    moneyXs.clear();
	    moneyYs.clear();
	    if (failTime > 5 && failTime != 30) {
	    	ogTime -=5;
	    	failTime = ogTime;
	    }
	    if (factor != 5) {
	    	factor -= 5;
	    }
	    failTimer();
	    start();
	}
	
	@FXML
	void failTimer() {
		if (timeline != null) {
	        timeline.stop(); 
	    }
		timer.setText(failTime + "s");
	    timeline = new Timeline(
	        new KeyFrame(Duration.seconds(1), event -> {
	        	if (failTime>0) {
	        		failTime--;
	        	}
	            timer.setText(failTime + "s");
	            if (failTime == 0) {
	            	timeline.stop();
	                endMenu();
	            }
	        })
	    );
	    timeline.setCycleCount(Timeline.INDEFINITE);
	    timeline.play();
	}
	
	@FXML
	void endMenu() {
		root.getChildren().clear();
		charView.setVisible(false);
	    x = 0;
	    y = 0;
	    moneyXs.clear();
	    moneyYs.clear();
	    root.getChildren().add(moneyView);
	    root.getChildren().add(panel);
	    panel.getChildren().clear();
	    panel.getChildren().add(bg);
	    bg.getChildren().clear();
		panel.setVisible(true);
		panel.setViewOrder(-1);
		bg.setVisible(true);
		panel.setViewOrder(-1);
		Label highScore = new Label("Score: " + moneyCount);
		highScore.setFont(Font.font("CaskaydiaCove NF", FontWeight.BOLD, 20));
		highScore.setTextFill(Color.WHITE);
		root.add(highScore, 3, 2, 4, 4);
		highScore.setViewOrder(-1);
		root.setAlignment(Pos.CENTER);
	}
	
	@FXML
	void startMenu() {
		panel.setVisible(true);
		panel.setViewOrder(-1);
	}
	
	@FXML
	void showInstructions() {
		startButton.setVisible(false);
		helpButton.setVisible(false);
		exitButton.setVisible(false);
		bg.setVisible(true);
	}
	
	@FXML
	void exit() {
		root.getChildren().clear();
		System.exit(0);
	}
	
	@FXML
	public boolean moneyGone() {
	    for (Node node : root.getChildren()) {
	        if (node instanceof ImageView && node != charView && node != enemyView) {
	            return false;
	        }
	    }
	    return true;
	}
	
	@FXML
	void removeAlg(ImageView keep1, ImageView keep2, int x1, int y1) {
		for (int j = 0; j < root.getChildren().size(); j++) {
	        var node = root.getChildren().get(j);
	        if (node instanceof ImageView && node != keep1 && node != keep2) {
	            int col = GridPane.getColumnIndex(node);
	            int row = GridPane.getRowIndex(node);
	            if (col == x1 && row == y1) {
	                root.getChildren().remove(node);
	                break;
	            }
	        }
	    }
	}

	@FXML
	void enemyPathfinding() {
		if (movement != null) {
	        movement.stop();
	    }
	    movement = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
	    	int enemyX = GridPane.getColumnIndex(enemyView);
		    int enemyY = GridPane.getRowIndex(enemyView);
	        int deltaX = Integer.compare(x, enemyX);
	        int deltaY = Integer.compare(y, enemyY);
	        enemyX += deltaX;
	        enemyY += deltaY;
	        root.getChildren().remove(enemyView);
	        root.add(enemyView, enemyX, enemyY);
	    }));
	    movement.setCycleCount(Timeline.INDEFINITE);
	    movement.play();
	    if (collision != null) {
	    	collision.stop();
	    }
	    collision = new Timeline(new KeyFrame(Duration.millis(200), e -> {
	    	int enemyX = GridPane.getColumnIndex(enemyView);
		    int enemyY = GridPane.getRowIndex(enemyView);
	        if (enemyX == x && enemyY == y) {
	            endMenu();
	            return;
	        }
	    }));
	    collision.setCycleCount(Timeline.INDEFINITE);
	    collision.play();
	}

}