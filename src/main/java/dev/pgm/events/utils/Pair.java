package dev.pgm.events.utils;

public class Pair<T, U> {

  public T first;
  public U second;

  public Pair(T first, U second) {
    this.first = first;
    this.second = second;
  }

  public static <T, U> Pair<T, U> create(T first, U second) {
    return new Pair<T, U>(first, second);
  }
}
