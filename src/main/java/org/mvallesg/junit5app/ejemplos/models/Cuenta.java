package org.mvallesg.junit5app.ejemplos.models;

import org.mvallesg.junit5app.ejemplos.exceptions.DineroInsuficienteException;

import java.math.BigDecimal;

public class Cuenta {

    private String persona;
    private BigDecimal saldo;
    private Banco banco;

    public Cuenta(String persona, BigDecimal saldo) {
        this.saldo = saldo;
        this.persona = persona;
    }

    public String getPersona() {
        return persona;
    }

    public void setPersona(String persona) {
        this.persona = persona;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Banco getBanco() {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }

    /*
        Los BigDecimal son inmutables, por lo que cuando se ejecutan métodos para cambiar su valor (substract(), add()...),
        en realidad, estos devuelven otro objeto BigDecimal con el resultado, por lo que en este caso,
        si se quiere modificar el saldo, hay que volver a asignarle a la variable el objeto que devuelve el método en cuestión.
         */
    public void retirada(BigDecimal cantidad){
        if(cantidad.compareTo(this.saldo)>0){
            throw new DineroInsuficienteException("Dinero insuficiente");
        }
        this.saldo = this.saldo.subtract(cantidad);
    }

    public void ingreso(BigDecimal cantidad){
        this.saldo = this.saldo.add(cantidad);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Cuenta c)){
            return false;
        }
        if(this.persona==null || this.saldo==null){
            return false;
        }
        return this.persona.equals(c.getPersona()) && this.saldo.equals(c.getSaldo());
    }
}
