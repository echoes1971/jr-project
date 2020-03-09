package ch.rra.rprj.model.core;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.UUID;

/*
* See: https://www.onlinetutorialspoint.com/hibernate/custom-generator-class-in-hibernate.html
*/

public class IdGenerator implements IdentifierGenerator {

    public String generateMyId() {
        UUID uuid = UUID.randomUUID();
        String ret = uuid.toString().replaceAll("-","");
        ret = ret.substring(ret.length()-16);
        //System.out.println("ret = " + ret + " (" + ret.length() + ")");
        return ret;
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws HibernateException {
        return generateMyId();
    }

    @Override
    public boolean supportsJdbcBatchInserts() {
        return false;
    }
}
