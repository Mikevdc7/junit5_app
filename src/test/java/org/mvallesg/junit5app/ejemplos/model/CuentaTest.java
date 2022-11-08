package org.mvallesg.junit5app.ejemplos.model;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.mvallesg.junit5app.ejemplos.exceptions.DineroInsuficienteException;
import org.mvallesg.junit5app.ejemplos.models.Banco;
import org.mvallesg.junit5app.ejemplos.models.Cuenta;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

/*
Como buena práctica, las clases y los métodos de test debe tener un modificador de acceso default
(se le llama comúnmente como modificador privado de package (es decir, protected,
que solamente lo podemos utilizar dentro del contexto del package)).
 */

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
/*
Esto no es recomendable, ya que hace que los test se ejecuten sobre la misma instancia de la clase, lo cual puede incitarnos
a usar el estado de esta durante los test. Los test se supone que deben ser independientes los unos de los otros, por lo que
el ciclo de vida de la instancia de los tests debería ser por método (TestInstance.Lifecycle.PER_METHOD -> si no ponemos esta
anotación, este está por defecto).
*/
class CuentaTest {
    Cuenta cuenta;

    @BeforeAll
    static void beforeAll(){
        System.out.println("Inicializando el test.");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("Finalizando el test.");
    }

    @BeforeEach
    void initMetodoTest(){
        this.cuenta = new Cuenta("Mike", new BigDecimal("1000.12345"));
        System.out.println("Iniciando el método.");
    }

    @AfterEach
    void tearDown(){
        System.out.println("Finalizando el método de prueba.");
    }

    @Test
    @DisplayName("Probando el nombre de la cuenta corriente.")
    void testNombreCuenta() {

        //cuenta.setPersona("Mike");
        String esperado = "Mike";
        /*
        Si el mensaje de error se pone tal cual, se va a crear la instancia con el texto aunque el test no falle, por lo que
        en el caso de haber muchos tests y cada uno de ellos con un mensaje personalizado de esta forma, se consumen recursos en vano.
        Para que solamente se cree la instancia cuando el test falle y no cuando pase, se debe usar una expresión lambda.
        * */
        assertNotNull(cuenta, "La cuenta no puede ser nula"); //Así se genera el string aunque el test pase (consume recursos en vano)
        String real = cuenta.getPersona();
        assertNotNull(real, () -> "El usuario de la cuenta no puede ser nulo");//Así solo se genera el string si el test falla
        assertEquals(esperado, real, () -> "El nombre de la cuenta no es el que se esperaba");
        //assertTrue(real.equals(esperado));
    }

    @Test
    @DisplayName("Probando que el saldo de la cuenta corriente no sea null y sea mayor que cero.")
    void testSaldoCuenta() {
        assertNotNull(cuenta.getSaldo());
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        //assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        //assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) >= 0);
    }

    @Test
    @DisplayName("Probando que dos cuentas con la misma información se consideran la misma con el método equals().")
    void testReferenciaCuenta() {
        cuenta = new Cuenta("Mike", new BigDecimal("8900.9997"));
        Cuenta cuenta2 = new Cuenta("Mike", new BigDecimal("8900.9997"));

        assertEquals(cuenta2, cuenta);
    }

    @Test
    @DisplayName("Probando que la retirada de saldo en una cuenta se realiza correctamente.")
    void testRetiradaCuenta() {
        cuenta.retirada(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.12345", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testDineroInsuficienteExceptionCuenta() {
        Exception exception = assertThrows(DineroInsuficienteException.class, ()-> cuenta.retirada(new BigDecimal("2000")));
        String real = exception.getMessage();
        String esperado = "Dinero insuficiente";
        assertEquals(esperado, real);
    }

    @Test
    void testIngresoCuenta() {
        cuenta.ingreso(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(1100, cuenta.getSaldo().intValue());
        assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testTransferirDineroCuentas() {
        Cuenta cuentaOrigen = new Cuenta("Dennis", new BigDecimal("2500"));
        Cuenta cuentaDestino = new Cuenta("Mike", new BigDecimal("12207.81"));

        Banco banco = new Banco();
        banco.setNombre("Santander");
        banco.transferir(cuentaOrigen, cuentaDestino, new BigDecimal(100));

        assertEquals("12307.81", cuentaDestino.getSaldo().toPlainString());
        assertEquals("2400", cuentaOrigen.getSaldo().toPlainString());
    }

    @Test
    // @Disabled -> Esta anotación hace que se ignore este test a la hora de pasarlo
    void testRelacionBancoCuentas() {
        // fail(); // -> este método fuerza el error, si se ejecuta, el test se marca como fallado.
        Cuenta cuenta1 = new Cuenta("Mike", new BigDecimal("3403.25"));
        Cuenta cuenta2 = new Cuenta("Mike 2", new BigDecimal("12207.81"));

        Banco banco = new Banco();
        banco.setNombre("Santander");
        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);

        assertAll(() -> {
            assertNotNull(banco.getCuentas());
        }, ()->{
            assertEquals(2, banco.getCuentas().size());
        }, ()->{
            assertEquals(cuenta1, banco.getCuentas().get(0));
        }, ()->{
            assertEquals(cuenta2, banco.getCuentas().get(1));
        }, ()->{
            assertNotNull(cuenta1.getBanco());
        }, ()->{
            assertEquals("Santander", cuenta1.getBanco().getNombre());
        }, ()->{
            assertNotNull(cuenta2.getBanco());
        }, ()->{
            assertEquals("Santander", cuenta2.getBanco().getNombre());
        }, () -> {
            assertEquals("Mike", banco.getCuentas().stream()
                    .filter(cuenta -> cuenta.getPersona()
                            .equalsIgnoreCase("Mike"))
                    .findFirst()
                    .get().getPersona());
        }, ()->{
            assertTrue(banco.getCuentas().stream()
                    .anyMatch(cuenta -> cuenta.getPersona()
                            .equalsIgnoreCase("Mike")));
        });
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void testSoloWindows() {
    }

    @Test
    @EnabledOnOs({OS.LINUX, OS.MAC})
    void testSoloLinuxMac() {
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void testNoWindows() {
    }

    @Test
    @EnabledOnJre(JRE.JAVA_8)
    void soloJdk8() {
    }

    @Test
    @EnabledOnJre(JRE.JAVA_18)
    void soloJdk18(){
    }

    @Test
    void imprimirSystemProperties() {
        Properties properties = System.getProperties();
        properties.forEach((k, v) -> System.out.println(k + ":" + v));
    }

    @Test
    @EnabledIfSystemProperty(named = "java.version", matches = "18.0.1")
    void testJavaVersion(){
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "USERDOMAIN", matches = "DESKTOP-P53KTTL")
    void imprimirVariablesAmbiente() {
        Map<String, String> environment = System.getenv();
        environment.forEach((k, v) -> System.out.println(k + ":" + v));
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSORS", matches = "16")
    void testProcesadores() {
    }

    @Test
    void testSaldoCuentaDev() {
        boolean esDev = "dev".equals(System.getProperty("ENV"));
        assumeTrue(esDev);
        /*
        Si el assumeTrue es verdadero, se deshabilita la prueba, ni se pasa ni se falla.
        el assumeTrue (de Assumption), asume una cierta condición para que se pueda ejecutar la prueba;
        si no se cumple, la deshabilita.
         */
        assertNotNull(cuenta.getSaldo());
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        //assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        //assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) >= 0);
    }

    @Test
    void testSaldoCuentaDev2() {
        boolean esDev = "dev".equals(System.getProperty("ENV"));
        assumingThat(esDev, () ->{
            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        });
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) >= 0);
    }


}