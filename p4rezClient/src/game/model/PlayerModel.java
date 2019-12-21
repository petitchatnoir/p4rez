package game.model;

import java.util.Random;

import javafx.scene.paint.Color;

public class PlayerModel {
	private String name;
	private Color color;
	
	public PlayerModel() {
		Random rand = new Random();
		this.name = "player" + rand.nextInt(421);
		this.color = Color.rgb(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
	}
	
	public PlayerModel(String name, Color color) {
		this.name = name;
		this.color = color;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Object[] getColorRGB() {
		Double x;
		Object rgb[] = new Object[3];
		
		x = this.color.getRed() * 255;
		rgb[0] = x.intValue();
		
		x = this.color.getGreen() * 255;
		rgb[1] = x.intValue();
		
		x = this.color.getBlue() * 255;
		rgb[2] = x.intValue();
		
		return rgb;
	}
}
