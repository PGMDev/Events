package dev.pgm.events.utils;

import static net.kyori.adventure.text.Component.text;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

public class Response {

  private final boolean allowed;
  private final Component component;
  private final String message;

  public Response(boolean allowed) {
    this.allowed = allowed;
    this.component = null;
    this.message = null;
  }

  public Response(boolean allowed, @Nullable Component message) {
    this.allowed = allowed;
    this.component = message;
    this.message = null;
  }

  public Response(boolean allowed, String message) {
    this.allowed = allowed;
    this.component = text(message);
    this.message = message;
  }

  public boolean isAllowed() {
    return this.allowed;
  }

  public boolean isDenied() {
    return !this.allowed;
  }

  public Component getComponent() {
    return component;
  }

  public String getMessage() {
    return message;
  }

  public static Response allow() {
    return new Response(true);
  }

  public static Response allow(Component message) {
    return new Response(true, message);
  }

  public static Response allow(String message) {
    return new Response(true, message);
  }

  public static Response deny() {
    return new Response(false);
  }

  public static Response deny(Component message) {
    return new Response(false, message);
  }

  public static Response deny(String message) {
    return new Response(false, message);
  }
}
