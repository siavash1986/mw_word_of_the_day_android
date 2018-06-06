package me.siavash.android.wotd.entities;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import me.siavash.android.wotd.util.Utils;


@Entity
public class Word implements Comparable<Word> {

  @NonNull
  @PrimaryKey
  private String date;

  private String title;

  private String attribute;

  private String syllables;

  private String definition;

  private String examples;

  private String didYouKnow;

  private String pronounceUrl;

  private boolean fav;

  private boolean importedToAnki;

  private long LastVisitTimeStamp;

  public Word() {
  }

  public boolean isFav() {
    return fav;
  }

  public void setFav(boolean fav) {
    this.fav = fav;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getAttribute() {
    return attribute;
  }

  public void setAttribute(String attribute) {
    this.attribute = attribute;
  }

  public String getSyllables() {
    return syllables;
  }

  public void setSyllables(String syllables) {
    this.syllables = syllables;
  }

  public String getDefinition() {
    return definition;
  }

  public void setDefinition(String definition) {
    this.definition = definition;
  }

  public String getExamples() {
    return examples;
  }

  public void setExamples(String examples) {
    this.examples = examples;
  }

  public String getDidYouKnow() {
    return didYouKnow;
  }

  public void setDidYouKnow(String didYouKnow) {
    this.didYouKnow = didYouKnow;
  }

  public String getPodcastUrl() {
    return Utils.getPodcastUri(this).toString();
  }

  public String getPronounceUrl() {
    return pronounceUrl == null ? "" : pronounceUrl;
  }

  public void setPronounceUrl(String pronounceUrl) {
    this.pronounceUrl = pronounceUrl;
  }

  public long getLastVisitTimeStamp() {
    return LastVisitTimeStamp;
  }

  public void setLastVisitTimeStamp(long lastVisitTimeStamp) {
    LastVisitTimeStamp = lastVisitTimeStamp;
  }

  public boolean isImportedToAnki() {
    return importedToAnki;
  }

  public void setImportedToAnki(boolean importedToAnki) {
    this.importedToAnki = importedToAnki;
  }

  @Override
  public String toString() {
    return "WordoftheDay{" +
        ", date= '" + date +
        ", title= '" + title + '\'' +
        ", attribute= '" + attribute + '\'' +
        ", syllables= '" + syllables + '\'' +
        ", definition= " + definition +
        ", examples= " + examples +
        ", didYouKnow= '" + didYouKnow + '\'' +
        ", podcastUrl= '" + getPodcastUrl() + '\'' +
        '}';
  }

  @Override
  public boolean equals(Object obj) {
    return obj != null &&
        obj.getClass() == this.getClass() && ((Word) obj).getDate().equals(this.getDate());
  }

  @Override
  public int hashCode() {
    return this.getDate().hashCode() + 13;
  }

  @Override
  public int compareTo(@NonNull Word o) {
    return this.date.compareTo(o.getDate());
  }
}