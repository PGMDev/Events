package dev.pgm.events.utils;

import javax.annotation.Nullable;
import tc.oc.pgm.lib.net.kyori.adventure.text.Component;

public class Response {

  boolean allowed;
  Component message = null;

  public Response(boolean allowed, @Nullable Component message) {
    this.allowed = allowed;
    this.message = message;
  }

  public boolean isAllowed() {
    return this.allowed;
  }

  public boolean isDenied() {
    return !this.allowed;
  }

  public Component getMessage() {
    return message;
  }

  public static Response allow() {
    return Response.allow(null);
  }

  public static Response allow(@Nullable Component message) {
    return new Response(true, message);
  }

  public static Response deny() {
    return Response.deny(null);
  }

  public static Response deny(@Nullable Component message) {
    return new Response(false, message);
  }
}
