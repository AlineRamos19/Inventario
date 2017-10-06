package br.com.udacity.inventario;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import br.com.udacity.inventario.DAO.ProdutoContrato;

public class EditorProduto extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXIST_PRODUCT_LOADER = 0;
    private Uri mProdutoUri;
    private boolean mAlteracaoProduto = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mAlteracaoProduto = true;
            return false;
        }
    };

    EditText nomeProduto, precoProduto, quantidadeProduto, fornecedorProduto;
    private String nome, fornecedor;
    private double preco;
    private int quantidade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_produto);

        Intent intent = getIntent();
        mProdutoUri = intent.getData();

        if (mProdutoUri == null) {
            setTitle(getString(R.string.act_bar_add_produto));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.act_bar_editar_produto));
            getSupportLoaderManager().initLoader(EXIST_PRODUCT_LOADER, null, this);
        }


        nomeProduto = (EditText) findViewById(R.id.nome_produto);
        precoProduto = (EditText) findViewById(R.id.preço_produto);
        quantidadeProduto = (EditText) findViewById(R.id.quantidade_produto);
        fornecedorProduto = (EditText) findViewById(R.id.fornecedor_produto);

        nomeProduto.setOnTouchListener(mTouchListener);
        precoProduto.setOnTouchListener(mTouchListener);
        quantidadeProduto.setOnTouchListener(mTouchListener);
        fornecedorProduto.setOnTouchListener(mTouchListener);
    }


    @Override
    public void onBackPressed() {
        if (!mAlteracaoProduto) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.alert_descartar_sair));
        builder.setPositiveButton(getString(R.string.alert_sair), discardButtonClickListener);
        builder.setNegativeButton(getString(R.string.alert_continuar_edicao), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.alert_deletar_produto));
        builder.setPositiveButton(getString(R.string.alert_deletar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteProduct();
            }
        });

        builder.setNegativeButton(getString(R.string.alert_cancelar_delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {

        if (mProdutoUri != null) {
            int rowsDeleted = getContentResolver().delete(mProdutoUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(EditorProduto.this, "Erro ao deletar produto", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(EditorProduto.this, "Produto deletado", Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_salvar, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (mProdutoUri == null) {
            MenuItem item = menu.findItem(R.id.action_delete);
            item.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.acao_salvar:
                if (TextUtils.isEmpty(nomeProduto.getText().toString()) ||
                        TextUtils.isEmpty(precoProduto.getText().toString()) ||
                        TextUtils.isEmpty(quantidadeProduto.getText().toString()) ||
                        TextUtils.isEmpty(fornecedorProduto.getText().toString())) {

                    Toast.makeText(EditorProduto.this, getString(R.string.toast_validar_campos),
                            Toast.LENGTH_SHORT).show();
                } else {
                    salvarProduto();
                    finish();
                }
                return true;

            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;

            case android.R.id.home:

                if (!mAlteracaoProduto) {
                    NavUtils.navigateUpFromSameTask(EditorProduto.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButton = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NavUtils.navigateUpFromSameTask(EditorProduto.this);
                    }
                };

                showUnsavedChangesDialog(discardButton);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void salvarProduto() {

        nome = nomeProduto.getText().toString().trim();
        preco = Double.parseDouble(precoProduto.getText().toString().trim());
        quantidade = Integer.parseInt(quantidadeProduto.getText().toString().trim());
        fornecedor = fornecedorProduto.getText().toString().trim();

        ContentValues values = new ContentValues();
        values.put(ProdutoContrato.ProdutoEntrada.COLUNA_NOME_PRODUTO, nome);
        values.put(ProdutoContrato.ProdutoEntrada.COLUNA_PRECO_PRODUTO, preco);
        values.put(ProdutoContrato.ProdutoEntrada.COLUNA_QUANTIDADE_PRODUTO, quantidade);
        values.put(ProdutoContrato.ProdutoEntrada.COLUNA_FORNECEDOR_PRODUTO, fornecedor);


        if (mProdutoUri == null) {
            getContentResolver().insert(ProdutoContrato.ProdutoEntrada.URI_CONTEUDO, values);
        } else {
            getContentResolver().update(mProdutoUri, values, null, null);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                ProdutoContrato.ProdutoEntrada._ID,
                ProdutoContrato.ProdutoEntrada.COLUNA_NOME_PRODUTO,
                ProdutoContrato.ProdutoEntrada.COLUNA_PRECO_PRODUTO,
                ProdutoContrato.ProdutoEntrada.COLUNA_QUANTIDADE_PRODUTO,
                ProdutoContrato.ProdutoEntrada.COLUNA_FORNECEDOR_PRODUTO
        };
        return new CursorLoader(this, mProdutoUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {

            int nameProd = cursor.getColumnIndex(ProdutoContrato.ProdutoEntrada.
                    COLUNA_NOME_PRODUTO);
            int priceProd = cursor.getColumnIndex(ProdutoContrato.ProdutoEntrada.
                    COLUNA_PRECO_PRODUTO);
            int quantityProd = cursor.getColumnIndex(ProdutoContrato.ProdutoEntrada.
                    COLUNA_QUANTIDADE_PRODUTO);
            int providerProd = cursor.getColumnIndex(ProdutoContrato.ProdutoEntrada.
                    COLUNA_FORNECEDOR_PRODUTO);

            String name = cursor.getString(nameProd);
            double price = cursor.getDouble(priceProd);
            int quantity = cursor.getInt(quantityProd);
            String provider = cursor.getString(providerProd);

            nomeProduto.setText(name);
            precoProduto.setText(Double.toString(price));
            quantidadeProduto.setText(Integer.toString(quantity));
            fornecedorProduto.setText(provider);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nomeProduto.setText("");
        precoProduto.setText("");
        quantidadeProduto.setText("");
        fornecedorProduto.setText("");

    }
}
