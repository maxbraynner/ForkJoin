package fork_join;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class BuscaRecursiva extends RecursiveTask<BigDecimal>{

	private static final long serialVersionUID = 1L;
	
	private File file;
	
	public BuscaRecursiva(String pDiretorio) {
		file = new File(pDiretorio);
	}
	
	public BuscaRecursiva(File pFile) {
		this.file = pFile;
	}
	
	@Override
	protected BigDecimal compute() {
		
		// caso seja um arquivo retorna seu tamanho
		if (file.isFile()) {
//			System.out.println("Arquivo: " + file.getPath() + " - " + file.length());
			return BigDecimal.valueOf(file.length());
		}
		
		/** Caso seja um diretporio irá varrer todos os seus filhos,
		 *  Sempre parando no caso base e verificando se há um arquivo.
		 */
		ArrayList<BuscaRecursiva> arrayBuscas = new ArrayList<BuscaRecursiva>();
		File[] files = file.listFiles();
		
		// Cria novos ramos de busca
		for (File fileAtual : files) {
			BuscaRecursiva novaBusca = new BuscaRecursiva(fileAtual);
			novaBusca.fork();
			arrayBuscas.add(novaBusca);
		}
		
		// Espera o resultado de cada ramo
		BigDecimal tamanho = BigDecimal.ZERO;
		for (BuscaRecursiva buscaRecursiva : arrayBuscas) {
			tamanho = tamanho.add(buscaRecursiva.join());
		}
		
		return tamanho;
	}
	
	public static void main(String[] args) {
		String diretorio = "/home/braynner/Downloads";
//		String diretorio = "C:\\Documents and Settings\\maxbm\\Meus documentos";
		
		BuscaRecursiva buscaRecursiva = new BuscaRecursiva(diretorio);
		
		// pool para criar as buscas com o número máximo de acordo com a quantidade de CPU's
		ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors() * 5);
		
		// Inicio da execução
		long timeInicio = System.currentTimeMillis();
		
		System.out.println(pool.invoke(buscaRecursiva) + " bytes ");
		
		// tempo total
		System.out.println(System.currentTimeMillis() - timeInicio);
	}

}
