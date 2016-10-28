package com.diegotori.app.zmdb.mvp.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MovieItem{
	@SerializedName("Description")
	@Expose
	private String Description;
	@SerializedName("Director")
	@Expose
	private String Director;
	@SerializedName("Rank")
	@Expose
	private int Rank;
	@SerializedName("Duration")
	@Expose
	private String Duration;
	@SerializedName("Actors")
	@Expose
	private List<String> Actors;
	@SerializedName("Id")
	@Expose
	private int Id;
	@SerializedName("Genres")
	@Expose
	private List<String> Genres;
	@SerializedName("Name")
	@Expose
	private String Name;

	public void setDescription(String Description){
		this.Description = Description;
	}

	public String getDescription(){
		return Description;
	}

	public void setDirector(String Director){
		this.Director = Director;
	}

	public String getDirector(){
		return Director;
	}

	public void setRank(int Rank){
		this.Rank = Rank;
	}

	public int getRank(){
		return Rank;
	}

	public void setDuration(String Duration){
		this.Duration = Duration;
	}

	public String getDuration(){
		return Duration;
	}

	public void setActors(List<String> Actors){
		this.Actors = Actors;
	}

	public List<String> getActors(){
		return Actors;
	}

	public void setId(int Id){
		this.Id = Id;
	}

	public int getId(){
		return Id;
	}

	public void setGenres(List<String> Genres){
		this.Genres = Genres;
	}

	public List<String> getGenres(){
		return Genres;
	}

	public void setName(String Name){
		this.Name = Name;
	}

	public String getName(){
		return Name;
	}

}