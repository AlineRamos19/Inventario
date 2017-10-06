package br.com.udacity.inventario;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import br.com.udacity.inventario.DAO.ProdutoContrato;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUTO_LOADER = 0;
    ProdutoAdapter mProdutoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorProduto.class);
                startActivity(intent);
            }
        });

        ListView listView = (ListView) findViewById(R.id.lista_produto);
        View listaVazia = findViewById(R.id.visualizao_vazia);
        listView.setEmptyView(listaVazia);

        mProdutoAdapter = new ProdutoAdapter(this, null);
        listView.setAdapter(mProdutoAdapter);
        listView.setDivider(null);
        registerForContextMenu(listView);

        getSupportLoaderManager().initLoader(PRODUTO_LOADER, null, this);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Selecione");
        menu.add(0, v.getId(), 0, "Editar");
        menu.add(0, v.getId(), 0, "Detalhes");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        long index = mProdutoAdapter.getItemId(menuInfo.position);

        if (item.getTitle() == "Editar") {
            Intent intent = new Intent(MainActivity.this, EditorProduto.class);

            Uri produtoUri = ContentUris.withAppendedId(ProdutoContrato.ProdutoEntrada.URI_CONTEUDO,
                    index);
            intent.setData(produtoUri);
            startActivity(intent);

        } if (item.getTitle() == "Detalhes"){
            Intent intent = new Intent(MainActivity.this, DetalhesProduto.class);

            Uri produtoUri = ContentUris.withAppendedId(ProdutoContrato.ProdutoEntrada.URI_CONTEUDO,
                    index);
            intent.setData(produtoUri);
            startActivity(intent);
        }
        return super.onContextItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                ProdutoContrato.ProdutoEntrada._ID,
                ProdutoContrato.ProdutoEntrada.COLUNA_NOME_PRODUTO,
                ProdutoContrato.ProdutoEntrada.COLUNA_PRECO_PRODUTO,
                ProdutoContrato.ProdutoEntrada.COLUNA_QUANTIDADE_PRODUTO};


        return new CursorLoader(this,
                ProdutoContrato.ProdutoEntrada.URI_CONTEUDO,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mProdutoAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProdutoAdapter.swapCursor(null);
    }
}
