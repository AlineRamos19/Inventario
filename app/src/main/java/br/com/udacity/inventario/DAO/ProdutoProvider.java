package br.com.udacity.inventario.DAO;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;


public class ProdutoProvider extends ContentProvider {


    public static final String LOG_TAG = ProdutoProvider.class.getSimpleName();
    private static final int PRODUTO = 100;
    private static final int PRODUTO_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(ProdutoContrato.AUTORIZACAO_CONTEUDO, ProdutoContrato.CAMINHO_PRODUTO, PRODUTO);
        sUriMatcher.addURI(ProdutoContrato.AUTORIZACAO_CONTEUDO, ProdutoContrato.CAMINHO_PRODUTO + "/#",
                PRODUTO_ID);
    }

    private ProdutoDbHelper mProdutoDbHelper;

    @Override
    public boolean onCreate() {
        mProdutoDbHelper = new ProdutoDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projecao, String selecao, String[] selecaoArgs, String sortOrder) {

        SQLiteDatabase db = mProdutoDbHelper.getReadableDatabase();

        Cursor cursor = null;

        int match = sUriMatcher.match(uri);

        switch (match) {
            case PRODUTO:
                cursor = db.query(ProdutoContrato.ProdutoEntrada.NOME_TABELA, projecao, selecao,
                        selecaoArgs, null, null, sortOrder);
                break;

            case PRODUTO_ID:
                selecao = ProdutoContrato.ProdutoEntrada._ID + "=?";
                selecaoArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ProdutoContrato.ProdutoEntrada.NOME_TABELA, projecao, selecao,
                        selecaoArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Erro Uri: " + uri);

        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUTO:
                return ProdutoContrato.ProdutoEntrada.CONTEUDO_LISTA;
            case PRODUTO_ID:
                return ProdutoContrato.ProdutoEntrada.CONTEUDO_ITEM;
            default:
                throw new IllegalArgumentException("Uri desconhecida " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUTO:
                return insertProduto(uri, contentValues);
            default:
                throw new IllegalArgumentException("Inserção não suportada for " + uri);
        }

    }

    private Uri insertProduto(Uri uri, ContentValues contentValues) {


        ProdutoDbHelper mDbHelper = new ProdutoDbHelper(getContext());

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long id = db.insert(ProdutoContrato.ProdutoEntrada.NOME_TABELA, null, contentValues);
        if (id == -1) {
            Log.e(LOG_TAG, "Falha na inserção da linha para " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        ProdutoDbHelper mDbHelper = new ProdutoDbHelper(getContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int rowsDelete;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUTO:
               rowsDelete = db.delete(ProdutoContrato.ProdutoEntrada.NOME_TABELA, selection,
                       selectionArgs);
                if (rowsDelete != 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDelete;

            case PRODUTO_ID:
                selection = ProdutoContrato.ProdutoEntrada._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDelete = db.delete(ProdutoContrato.ProdutoEntrada.NOME_TABELA, selection,
                        selectionArgs);
                if (rowsDelete != 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDelete;
            default:
                throw new IllegalArgumentException("Delete não suportado para " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUTO:
                return updateProduto(uri, contentValues, selection, selectionArgs);

            case PRODUTO_ID:
                selection = ProdutoContrato.ProdutoEntrada._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduto(uri, contentValues, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Update nao suportada para " + uri);
        }
    }

    private int updateProduto(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        if (contentValues.containsKey(ProdutoContrato.ProdutoEntrada.COLUNA_NOME_PRODUTO)) {
            String name = contentValues.getAsString(ProdutoContrato.ProdutoEntrada.COLUNA_NOME_PRODUTO);
            if (name == null) {
                throw new IllegalArgumentException("Produto requer um nome!");
            }
        }

        if (contentValues.containsKey(ProdutoContrato.ProdutoEntrada.COLUNA_PRECO_PRODUTO)) {
            Double preco = contentValues.getAsDouble(ProdutoContrato.ProdutoEntrada.COLUNA_PRECO_PRODUTO);
            if (preco != null && preco < 0) {
                throw new IllegalArgumentException("Produto requer um preço!");
            }
        }

        if (contentValues.containsKey(ProdutoContrato.ProdutoEntrada.COLUNA_QUANTIDADE_PRODUTO)) {
            Integer quantidade = contentValues.getAsInteger(ProdutoContrato.ProdutoEntrada.
                    COLUNA_QUANTIDADE_PRODUTO);
            if (quantidade != null && quantidade < 0) {
                throw new IllegalArgumentException("Produto requer uma quantidade!");
            }
        }

        if (contentValues.containsKey(ProdutoContrato.ProdutoEntrada.COLUNA_FORNECEDOR_PRODUTO)) {
            String fornecedor = contentValues.getAsString(ProdutoContrato.ProdutoEntrada.
                    COLUNA_FORNECEDOR_PRODUTO);
            if (fornecedor == null) {
                throw new IllegalArgumentException("Produto requer um fornecedor!");
            }
        }

        if (contentValues.size() == 0) {
            return 0;
        }

        ProdutoDbHelper mDbHelper = new ProdutoDbHelper(getContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

       int rowsUpdate = db.update(ProdutoContrato.ProdutoEntrada.NOME_TABELA, contentValues,
               selection, selectionArgs);

        if(rowsUpdate != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdate;
    }
}
