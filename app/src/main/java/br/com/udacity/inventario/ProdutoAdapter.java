package br.com.udacity.inventario;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import br.com.udacity.inventario.DAO.ProdutoContrato;


public class ProdutoAdapter extends CursorAdapter {

    public ProdutoAdapter(Context context, Cursor cursor) {
        super(context, cursor , 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.item_produto, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView nome = (TextView) view.findViewById(R.id.nome_item);
        TextView preco = (TextView) view.findViewById(R.id.preco_item);
        TextView quantidade = (TextView) view.findViewById(R.id.quantidade_item);

        int nomeColunaIndex = cursor.getColumnIndex(ProdutoContrato.ProdutoEntrada.
                COLUNA_NOME_PRODUTO);
        int precoColunaIndex = cursor.getColumnIndex(ProdutoContrato.ProdutoEntrada.
                COLUNA_PRECO_PRODUTO);
        int quantidadeColunaIndex = cursor.getColumnIndex(ProdutoContrato.ProdutoEntrada.
                COLUNA_QUANTIDADE_PRODUTO);


        String nomeProduto = cursor.getString(nomeColunaIndex);
        String precoProduto = String.valueOf(cursor.getDouble(precoColunaIndex));
        Integer quantidadeProduto = cursor.getInt(quantidadeColunaIndex);

        nome.setText(nomeProduto);
        preco.setText(String.valueOf(precoProduto));
        quantidade.setText(String.valueOf(quantidadeProduto));
    }
}
