package com.api;

public interface Cancellable {
	void cancel();
	boolean isCancelled();
}
