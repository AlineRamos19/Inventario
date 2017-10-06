package br.com.udacity.inventario.DAO;


import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class ProdutoContrato {

    private ProdutoContrato(){
    }

    public static final String AUTORIZACAO_CONTEUDO = "br.com.udacity.inventario";
    public static final Uri CONTEUDO_BASE_URI = Uri.parse("content://" + AUTORIZACAO_CONTEUDO);
    public static final String CAMINHO_PRODUTO = "produtos";


    public static final class ProdutoEntrada implements BaseColumns {

        public static final Uri URI_CONTEUDO = Uri.withAppendedPath(CONTEUDO_BASE_URI, CAMINHO_PRODUTO);

        public static final String CONTEUDO_LISTA = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                AUTORIZACAO_CONTEUDO + "/" + CAMINHO_PRODUTO;

        public static final String CONTEUDO_ITEM = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                AUTORIZACAO_CONTEUDO + "/" + CAMINHO_PRODUTO;

        public static final String NOME_TABELA = "produtos";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUNA_NOME_PRODUTO = "nomeProduto";
        public static final String COLUNA_PRECO_PRODUTO = "precoProduto";
        public static final String COLUNA_QUANTIDADE_PRODUTO = "quantidadeProduto";
        public static final String COLUNA_FORNECEDOR_PRODUTO = "fornecedorProduto";

    }
}
