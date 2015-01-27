package com.luwei.domain;

public class FittingMainifest {
	int id;
	String name;
	String image_url;
	String function;
	String time;
	int count;
	
	public FittingMainifest() {
		super();
	}

	public FittingMainifest(int id, String name, String image_url,
			String function, String time, int count) {
		super();
		this.id = id;
		this.name = name;
		this.image_url = image_url;
		this.function = function;
		this.time = time;
		this.count = count;
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getImage_url() {
		return image_url;
	}
	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}
	public String getFunction() {
		return function;
	}
	public void setFunction(String function) {
		this.function = function;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "FittingMainifest [id=" + id + ", name=" + name + ", image_url="
				+ image_url + ", function=" + function + ", time=" + time
				+ ", count=" + count + "]";
	}
}
