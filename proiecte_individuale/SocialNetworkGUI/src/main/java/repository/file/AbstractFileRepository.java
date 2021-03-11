package socialnetwork.repository.file;

import socialnetwork.domain.Entity;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.memory.InMemoryRepository;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID,E> {
    String fileName;
    public AbstractFileRepository(String fileName, Validator<E> validator) {
        super(validator);
        this.fileName=fileName;
        loadData();
    }

    private void loadData() {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String linie;
            while((linie=br.readLine())!=null){
                List<String> attr=Arrays.asList(linie.split(";"));
                E e=extractEntity(attr);
                super.save(e);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  extract entity  - template method design pattern
     *  creates an entity of type E having a specified list of @code attributes
     * @param attributes is a list of strings
     * @return an entity of type E
     */
    public abstract E extractEntity(List<String> attributes);

    protected abstract String createEntityAsString(E entity);

    @Override
    public E save(E entity){
        E e=super.save(entity);
        //writeToFile(entity);
        reWriteFile();
        return e;
    }

    @Override
    public E delete(ID id) {
        E entity = super.delete(id);
        reWriteFile();
        return entity;
    }

    @Override
    public E update(E entity) {
        E e=super.update(entity);
        reWriteFile();
        return e;
    }

    protected void writeToFile(E entity){
        try (BufferedWriter bW = new BufferedWriter(new FileWriter(fileName,true))) {
            bW.write(createEntityAsString(entity));
            bW.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void reWriteFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, false))) {
            // Empties the file
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        for (Map.Entry<ID, E> entity : entities.entrySet()) {
            writeToFile(entity.getValue());
        }
    }
}

