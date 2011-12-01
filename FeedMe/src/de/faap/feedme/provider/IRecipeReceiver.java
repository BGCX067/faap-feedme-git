package de.faap.feedme.provider;

import java.util.Collection;

public interface IRecipeReceiver {
    public void addTable(String tableName, Collection<String[]> tableContents);

    public void open();

    public void close();
}
