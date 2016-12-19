package me.ranol.scriptingspells.exceptions;

public class InvalidParticleException extends Exception{
	String particleName;
	public InvalidParticleException(String particleName) {
		this.particleName = particleName;
	}
}
