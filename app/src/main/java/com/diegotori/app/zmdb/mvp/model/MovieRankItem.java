package com.diegotori.app.zmdb.mvp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MovieRankItem{
	@SerializedName("Rank")
	@Expose
	private int Rank;
	@SerializedName("Id")
	@Expose
	private int Id;
	@SerializedName("Name")
	@Expose
	private String Name;

	public void setRank(int Rank){
		this.Rank = Rank;
	}

	public int getRank(){
		return Rank;
	}

	public void setId(int Id){
		this.Id = Id;
	}

	public int getId(){
		return Id;
	}

	public void setName(String Name){
		this.Name = Name;
	}

	public String getName(){
		return Name;
	}

}