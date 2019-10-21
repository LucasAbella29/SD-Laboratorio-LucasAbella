package ar.edu.unp.madryn.livremarket.common.sm;

import lombok.Getter;

import java.util.Objects;

/**
 * @author iMinecrafting
 * Representa una transicion de la maquina de estados.
 */
public abstract class Transition {
    @Getter
    private State from;
    @Getter
    private State to;

    public Transition(State from, State to) {
        this.from = from;
        this.to = to;
    }

    /**
     * Condicion que debe cumplirse para que la transicion pueda realizarse.
     * @return Verdadero si la condicion se cumple.
     */
    public abstract Boolean condition();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transition)) return false;
        Transition that = (Transition) o;
        return Objects.equals(from, that.from) &&
                Objects.equals(to, that.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }
}
