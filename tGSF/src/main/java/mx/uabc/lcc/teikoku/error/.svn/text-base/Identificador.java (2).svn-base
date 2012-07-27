package mx.uabc.lcc.teikoku.error;

import java.util.UUID;

public class Identificador {
    //
    private static Identificador instanciaUnica;
    private static Integer id;
    private Identificador()
    {
        id=0;
    }

    public static Identificador getInstance()
    {
        // put your code here
        if(instanciaUnica==null)
        {
            instanciaUnica = new Identificador();
        }
        return instanciaUnica;
    }
    public UUID getUUID()
    {
        id++;
        return UUID.fromString(id.toString());
    }
    
    public UUID getRandomUUID()
    {
        return UUID.randomUUID();
    }
        
}
