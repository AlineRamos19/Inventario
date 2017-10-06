package br.com.udacity.inventario.DAO;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ProdutoDbHelper extends SQLiteOpenHelper {

    public static final String NOME_BANCO = "produto.db";
    public static final int VERSAO_BANCO = 6;

    private static final String SQL_CRIAR_TABELA =
            "CREATE TABLE " + ProdutoContrato.ProdutoEntrada.NOME_TABELA + "(" +
            ProdutoContrato.ProdutoEntrada._ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
            ProdutoContrato.ProdutoEntrada.COLUNA_NOME_PRODUTO + " TEXT NOT NULL , " +
            ProdutoContrato.ProdutoEntrada.COLUNA_PRECO_PRODUTO + " REAL NOT NULL , " +
            ProdutoContrato.ProdutoEntrada.COLUNA_QUANTIDADE_PRODUTO + " INTEGER NOT NULL , " +
            ProdutoContrato.ProdutoEntrada.COLUNA_FORNECEDOR_PRODUTO + " TEXT NOT NULL );" ;

    private static final String SQL_DELETAR =
            "DROP TABLE IF EXISTS " + ProdutoContrato.ProdutoEntrada.NOME_TABELA;

    public ProdutoDbHelper(Context context) {
        super(context, NOME_BANCO, null, VERSAO_BANCO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CRIAR_TABELA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versaoAntiga, int novaVersao) {
        db.execSQL(SQL_DELETAR);
        onCreate(db);
    }
}

