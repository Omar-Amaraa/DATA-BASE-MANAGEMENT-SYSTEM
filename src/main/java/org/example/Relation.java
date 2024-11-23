
package org.example;

import java.util.ArrayList;
import java.util.List;
import java.nio.ByteBuffer;


public class Relation {
    private  String nomrelation;
    private  int nbcolonnes;
    private  List<ColInfo> colonnes;
    private DiskManager diskManager;
    private BufferManager bufferManager;
    private HeaderPage headerPageTable;


    public Relation(String n,int nbcolonnes,int indexFichierRelation,DiskManager diskManager,BufferManager bufferManager) {

        this.nomrelation=n;
        this.nbcolonnes=nbcolonnes;
        colonnes = new ArrayList<>();
        this.diskManager=diskManager;
        this.bufferManager=bufferManager;
        headerPageTable=new HeaderPage(indexFichierRelation);

    }

    public  String getNomrelation() {
        return nomrelation;
    }

    public  int getNbcolonnes() {
        return nbcolonnes;
    }

    public  List<ColInfo> getColonnes() {
        return colonnes;
    }

    public  void setNomrelation(String nomrelation) {
        this.nomrelation = nomrelation;
    }

    public  void setNbcolonnes(int nbcolonnes) {
        this.nbcolonnes = nbcolonnes;
    }

    public void ajouterColonne(ColInfo colInfo) {
        colonnes.add(colInfo);
    }

    public int writeRecordToBuffer(Record record, ByteBuffer buffer, int pos) {
        int initialPos = pos;

        for (int i = 0; i < colonnes.size(); i++) {
            ColInfo colInfo = colonnes.get(i);
            Object valeur = record.getValeurs().get(i);

            switch (colInfo.getType()) {
                case INT:
                    buffer.putInt(pos, Integer.parseInt(valeur.toString()));
                    pos += Integer.BYTES;
                    break;
                case REAL:
                    buffer.putFloat(pos, Float.parseFloat(valeur.toString()));
                    pos += Float.BYTES;
                    break;
                case CHAR:
                    String charValue = valeur.toString();
                    for (int j = 0; j < colInfo.getTailleMax(); j++) {
                        char c = j < charValue.length() ? charValue.charAt(j) : '\0';
                        buffer.putChar(pos, c);
                        pos += Character.BYTES;
                    }
                    break;
                case VARCHAR:
                    String varcharValue = valeur.toString();
                    for (int j = 0; j < varcharValue.length(); j++) {
                        buffer.putChar(pos, varcharValue.charAt(j));
                        pos += Character.BYTES;
                    }
                    break;
            }
        }

        return pos - initialPos; // Taille totale écrite
    }

    public int readFromBuffer(Record record, ByteBuffer buffer, int pos) {
        int initialPos = pos;

        for (ColInfo colInfo : colonnes) {
            switch (colInfo.getType()) {
                case INT:
                    record.ajouterValeur(buffer.getInt(pos));
                    pos += Integer.BYTES;
                    break;
                case REAL:
                    record.ajouterValeur(buffer.getFloat(pos));
                    pos += Float.BYTES;
                    break;
                case CHAR:
                    StringBuilder charValue = new StringBuilder();
                    for (int j = 0; j < colInfo.getTailleMax(); j++) {
                        char c = buffer.getChar(pos);
                        if (c != '\0') charValue.append(c);
                        pos += Character.BYTES;
                    }
                    record.ajouterValeur(charValue.toString());
                    break;
                case VARCHAR:
                    StringBuilder varcharValue = new StringBuilder();
                    while (pos < buffer.limit()) {
                        char c = buffer.getChar(pos);
                        if (c == '\0') break;
                        varcharValue.append(c);
                        pos += Character.BYTES;
                    }
                    record.ajouterValeur(varcharValue.toString());
                    break;
            }
        }

        return pos - initialPos; // Taille totale lue
    }

    /**
     * Methode pour créer une dataPage
     *
     */
    public void addDataPage(){
        //allouer une nouvelle page via AllocPage du DiskManager
        PageId newPageVide =diskManager.AllocPage();
        //actualiser le Page Directory en prenant en compte cette page
        headerPageTable.addPageToPageDirectory(newPageVide);
        //affichage
        System.out.println("Added a new data page at:\nPageIndex: "+ newPageVide.getPageIdx()+"\nFileIndex:"+newPageVide.getFileIdx() );

    }
    public PageId getFreeDataPageId(int sizeRecord){
        int normalPageSize = diskManager.getDbConfiginstance().getPagesize();//taille normale
        int UsablePageSize = normalPageSize-((8*PageId.getNbSlot())+8);//taille disponible pour ecrire les records apres avoir enlevé, l'espace
        //nécéssaire pour mettre la metaData(debut fin + nb d'entrées slot dir, pos début espace dispo)
        for (PageId p : headerPageTable.getPageDirectory()){//grace au header page on peut parcourir la liste des pages ( PageId) dans le
            //fichier contenant la relation (table Etudiant par exemple)
            int freeSpace = 0;// on compte maintenant l'espace disponible
            for (int i : p.getBitMap()){
                if (i ==0){//grace a la bitmap, on peut trouver les slots qui sont vides (leurs bit sont remis à 0) donc on va additionner leurs tailles
                    //pour savoir la taille exacte de l'espace disponible dans notre page
                    freeSpace+=UsablePageSize/PageId.getNbSlot();
                }
            }//si on l'espace est suffisant, on retourne p le PageId de la page où on a trouvé assez d'espace.
            if(freeSpace>= sizeRecord) {
                return p;
            }

        }//null sinon
        return null;
    }


}

