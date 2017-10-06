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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import br.com.udacity.inventario.DAO.ProdutoContrato;


public class Detalhes_Produto extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER = 0;
    private int mQuantidadeCompra = 0;
    Button comprarMais, addQuantidadeCompra, subQuantidadeCompra, comprar;

    TextView nome, preco, quantidade, fornecedor, quantidadeSelecionadaCompra;
    Uri detalheUri;
    Button btnDeletar;
    private int mQuantidade;
    private String mNome;
    private String mFornecedor;
    private double mPreco;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalhe_produto);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        nome = (TextView) findViewById(R.id.nome_produto_detalhe);
        preco = (TextView) findViewById(R.id.preco_produto_detalhe);
        quantidade = (TextView) findViewById(R.id.quantidade_produto_detalhe);
        fornecedor = (TextView) findViewById(R.id.fornecdor_produto_detalhe);
        quantidadeSelecionadaCompra = (TextView) findViewById(R.id.quantidade_escolhida_compra);
        btnDeletar = (Button) findViewById(R.id.btn_deletar);

        btnDeletar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog();
            }
        });

        comprarMais = (Button) findViewById(R.id.comprar_mais);
        comprarMais.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.pedido_compra)
                        + mFornecedor);
                intent.putExtra(Intent.EXTRA_TEXT, " " + criarOrdemCompra());
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        addQuantidadeCompra = (Button) findViewById(R.id.add_quantidade_compra);
        addQuantidadeCompra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aumentarQuantidade();
            }
        });

        subQuantidadeCompra = (Button) findViewById(R.id.sub_quantidade_compra);
        subQuantidadeCompra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                diminuirQuantidade();
            }
        });

        comprar = (Button) findViewById(R.id.comprar);
        comprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculoDisponivel();
                atualizarQuantidade();
            }
        });

        Intent intent = getIntent();
        detalheUri = intent.getData();

        if (detalheUri == null) {
            return;
        } else {
            getSupportLoaderManager().initLoader(LOADER, null, this);
        }
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

        if (detalheUri != null) {
            int rowsDeleted = getContentResolver().delete(detalheUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, "Erro ao deletar produto", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Produto deletado", Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    private String criarOrdemCompra() {
        String ordemCompra = "------- \n" +
                getString(R.string.label_descricao_detalhe_produto) + ": " + mNome + "\n" +
                getString(R.string.label_preco) + " " +
                getString(R.string.label_cifrao) + ": " + +mPreco + "\n" +
                getString(R.string.label_quantidade) + " " +
                getString(R.string.definir_quantidade) + "\n" +
                getString(R.string.label_fornecedor) + ": " + mFornecedor;

        return ordemCompra;
    }

    private void atualizarQuantidade() {

        ContentValues valores = new ContentValues();
        valores.put(ProdutoContrato.ProdutoEntrada.COLUNA_QUANTIDADE_PRODUTO, mQuantidade);

        getContentResolver().update(detalheUri, valores, null, null);
    }

    public void diminuirQuantidade() {
        if (mQuantidadeCompra <= 1) {
            Toast.makeText(Detalhes_Produto.this, getString(R.string.toast_quantidade_minima), Toast.LENGTH_SHORT).show();
            return;
        }
        mQuantidadeCompra = mQuantidadeCompra - 1;
        exibirQuantidade(mQuantidadeCompra);
    }

    public void aumentarQuantidade() {
        if (mQuantidadeCompra >= mQuantidade) {
            Toast.makeText(Detalhes_Produto.this, getString(R.string.toast_quantidade_maxima),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        mQuantidadeCompra = mQuantidadeCompra + 1;
        exibirQuantidade(mQuantidadeCompra);
    }

    private void exibirQuantidade(int quantidade) {
        quantidadeSelecionadaCompra.setText(String.valueOf(quantidade));
    }

    private int calculoDisponivel() {
        mQuantidade = mQuantidade - mQuantidadeCompra;
        return mQuantidade;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projecao = {
                ProdutoContrato.ProdutoEntrada._ID,
                ProdutoContrato.ProdutoEntrada.COLUNA_NOME_PRODUTO,
                ProdutoContrato.ProdutoEntrada.COLUNA_PRECO_PRODUTO,
                ProdutoContrato.ProdutoEntrada.COLUNA_QUANTIDADE_PRODUTO,
                ProdutoContrato.ProdutoEntrada.COLUNA_FORNECEDOR_PRODUTO
        };
        return new CursorLoader(this, detalheUri, projecao, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {

            int nomeProduto = cursor.getColumnIndex(ProdutoContrato.ProdutoEntrada.
                    COLUNA_NOME_PRODUTO);
            int precoProduto = cursor.getColumnIndex(ProdutoContrato.ProdutoEntrada.
                    COLUNA_PRECO_PRODUTO);
            int quantidadeProduto = cursor.getColumnIndex(ProdutoContrato.ProdutoEntrada.
                    COLUNA_QUANTIDADE_PRODUTO);
            int fornecedorProduto = cursor.getColumnIndex(ProdutoContrato.ProdutoEntrada.
                    COLUNA_FORNECEDOR_PRODUTO);

            mNome = cursor.getString(nomeProduto);
            mPreco = cursor.getDouble(precoProduto);
            mQuantidade = cursor.getInt(quantidadeProduto);
            mFornecedor = cursor.getString(fornecedorProduto);

            nome.setText(mNome);
            preco.setText(Double.toString(mPreco));
            quantidade.setText(Integer.toString(mQuantidade));
            fornecedor.setText(mFornecedor);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nome.setText("");
        preco.setText("");
        quantidade.setText("");
        fornecedor.setText("");
    }
}
