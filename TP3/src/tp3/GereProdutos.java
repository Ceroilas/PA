package tp3;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Classe que faz a gest�o de produtos
 * Insere, lista, pesquisa e verifica produtos na/da base de dados.
 * @author Goncalo-PC
 *
 */
public class GereProdutos {

	Connection conn;
    Statement st;
    ResultSet rs;
    String ip, porto, nomebd, login, pass;
    
    JavaProperties file = new JavaProperties();
    
    /**
     * Construtor da classe Gereprodutos que recebe como parametros 
	 * os dados para poder acessar a Base de Dados
     * @param aIP
     * @param aPorto
     * @param aNomeBD
     * @param aLogin
     * @param aPass
     */
    GereProdutos(String aIP, String aPorto, String aNomeBD, String aLogin, String aPass){
    	
    	if(file.leFicheiroTexto("ip") == null ){

        	file.escreveFicheiroTexto(aIP, aPorto, aNomeBD, aLogin, aPass);
    	}
    }
    
    GereProdutos(){
    	
    }
    
    /**
     * Este m�todo serve para inserir um produto na base de dados
     * Recebe como Parametros de entrada: 
     * aProduto - objeto do tipo produtos que contem todos os dados de um produto.
     * aLogin - String que contem o login do fabricante que introduziu o produto.
     * @param aProduto
     * @param aLogin
     */
    public void insereProduto(Produto aProduto, String aLogin){//adicionar a categoria

    	int u_id = getUID(aLogin);
    	getJavaProperties();
    	try {
    		Class.forName("com.mysql.jdbc.Driver").newInstance();
    		conn = DriverManager.getConnection("jdbc:mysql://"+ip+":"+porto+"/"+nomebd, login, pass);
    		
    		st = conn.createStatement();

    		
    		st.execute("INSERT INTO produtos(P_DESIGNACAO, P_FABRICANTE, P_PESO, P_PRECO, P_SKU, P_LOTE, P_DATAPRODUCAO, P_STOCK, P_CATEGORIA, U_ID)"
    				+ "VALUES ('"+ aProduto.getDesignacao() +"', '" + aProduto.getFabricante()+"', '" + aProduto.getPeso()+"', '" + aProduto.getPreco()+"', '" + aProduto.getSKU()+"', '" + aProduto.getLote()+"', '" + aProduto.getDataProducao()+"', '" + aProduto.getStock()+"', '"+ aProduto.getCategoria()+ "','"+ u_id +"');");
    		
    		st.close();

    	} catch (SQLException e) {
    		System.out.println("!! SQL Exception !!\n"+e);
    		e.printStackTrace();
    	} catch (ClassNotFoundException e) {
    		System.out.println("!! Class Not Found. Unable to load Database Drive !!\n"+e);
    	} catch (IllegalAccessException e) {
    		System.out.println("!! Illegal Access !!\n"+e);
    	} catch (InstantiationException e) {
    		System.out.println("!! Class Not Instanciaded !!\n"+e);
    	} finally {
    		if (st != null) {
    			try {
    				st.close();
    			} catch (Exception e) {
    				System.out.println("!! Exception returning statement !!\n"+e);
    			}
    		}
    		if (conn != null) {
    			try {
    				conn.close();
    			} catch (Exception e) {
    				System.out.println("!! Exception closing DB connection !!\n"+e);
    			}
    		}
    	} // end of finally
    }
    
    /**
     * Este m�todo serve para listar todos os produtos.
     * Caso o utilizador pretenda list�-los por ordem, o parametro de entrada aOrdena vem com os dados para ordena��o
     * O parametro aLogin - serve para verificar se o utilizador que pediu a listagem � um cliente ou funcionario, caso seja apenas lista os seus produtos.
     * @param aOrdena
     * @param aLogin
     * @return
     */
    public String listaProdutos(String aOrdena, String aLogin){
   
    	GereUtilizadores uti = new GereUtilizadores();
    	ResultSet rs1;
    	Statement st1;
    	String string = "";
    	boolean bool = true;
    	
    	getJavaProperties();
    	try {
    		Class.forName("com.mysql.jdbc.Driver").newInstance();
    		conn = DriverManager.getConnection("jdbc:mysql://"+ip+":"+porto+"/"+nomebd, login, pass);
    		
    		if(!aOrdena.equals("")){
    			aOrdena = "ORDER BY " + aOrdena;
    		}
    		
    		st = conn.createStatement();

    		rs = st.executeQuery("SELECT * " 
    				+ "FROM produtos " + string + aOrdena + ";");

    		String output = "";
    		int i = 0;
    		if(rs == null ){
    			output="Sem produtos registadas";
    		} else {
    			i++;
    			while (rs.next()) {
    					output += "        Produto        \nDesignacao: " + rs.getString("p_designacao") + "\nFabricante: " + rs.getString("p_fabricante") 
    					+ "\nPeso: " + rs.getString("p_peso") + "\nPre�o: " + rs.getString("p_preco")
    					+ "\nCodigo Sku: " + rs.getString("p_sku") + "\nLote: " + rs.getString("p_lote") + "\n"; 
    				}
    				if(i == 10){
						i=0;
    					output += "Limite de 10!";
    				}
    			} 
			if(output.equals("")){
				if(bool){
					output = "N�o tem produtos registadas no sistema!";
				} else {
					output = "N�o tem produtos registadas em seu nome!";
				}
			}
    		output += "Limite de 10!";
    		return output;

    	} catch (SQLException e) {
    		System.out.println("!! SQL Exception !!\n"+e);
    		e.printStackTrace();
    	} catch (ClassNotFoundException e) {
    		System.out.println("!! Class Not Found. Unable to load Database Drive !!\n"+e);
    	} catch (IllegalAccessException e) {
    		System.out.println("!! Illegal Access !!\n"+e);
    	} catch (InstantiationException e) {
    		System.out.println("!! Class Not Instanciaded !!\n"+e);
    	} finally {
    		if (st != null) {
    			try {
    				st.close();
    			} catch (Exception e) {
    				System.out.println("!! Exception returning statement !!\n"+e);
    			}
    		}
    		if (conn != null) {
    			try {
    				conn.close();
    			} catch (Exception e) {
    				System.out.println("!! Exception closing DB connection !!\n"+e);
    			}
    		}
    	} 
    	
    	return "";    	
    }
    
    
    /**
     * Este m�todo devolve um inteiro com o id de um utilizador.
     * Isto � feito de um login que vem como parametro de entrada.
     * @param aLogin
     * @return
     */
    
    public int getUID(String aLogin){
    	
    	int u_id = 0;
    	try {
    		Class.forName("com.mysql.jdbc.Driver").newInstance();
    		conn = DriverManager.getConnection("jdbc:mysql://"+ip+":"+porto+"/"+nomebd, login, pass);
    		
    		st = conn.createStatement();
    		
    		rs = st.executeQuery("SELECT * FROM utilizadores WHERE U_LOGIN = '"+ aLogin +"';");
    		
			rs.next();
			u_id = rs.getInt("U_ID");
			    		
    	
    		st.close();

    	} catch (SQLException e) {
    		System.out.println("!! SQL Exception !!\n"+e);
    		e.printStackTrace();
    	} catch (ClassNotFoundException e) {
    		System.out.println("!! Class Not Found. Unable to load Database Drive !!\n"+e);
    	} catch (IllegalAccessException e) {
    		System.out.println("!! Illegal Access !!\n"+e);
    	} catch (InstantiationException e) {
    		System.out.println("!! Class Not Instanciaded !!\n"+e);
    	} finally {
    		if (st != null) {
    			try {
    				st.close();
    			} catch (Exception e) {
    				System.out.println("!! Exception returning statement !!\n"+e);
    			}
    		}
    		if (conn != null) {
    			try {
    				conn.close();
    			} catch (Exception e) {
    				System.out.println("!! Exception closing DB connection !!\n"+e);
    			}
    		}
    	} // end of finally
    	return u_id;
    }
    
    /**
     * Este m�todo devolve um inteiro com o id de um produto.
     * Designacao vem como par�metro de entrada.
     * @param aLDesignacao
     * @return
     */
    
    public int getPID(String aDesignacao){
    	
    	int p_id = 0;
    	try {
    		Class.forName("com.mysql.jdbc.Driver").newInstance();
    		conn = DriverManager.getConnection("jdbc:mysql://"+ip+":"+porto+"/"+nomebd, login, pass);
    		
    		st = conn.createStatement();
    		
    		rs = st.executeQuery("SELECT * FROM produtos WHERE P_DESIGNACAO = '"+ aDesignacao +"';");
    		
			rs.next();
			p_id = rs.getInt("P_ID");
			    		
    	
    		st.close();

    	} catch (SQLException e) {
    		System.out.println("!! SQL Exception !!\n"+e);
    		e.printStackTrace();
    	} catch (ClassNotFoundException e) {
    		System.out.println("!! Class Not Found. Unable to load Database Drive !!\n"+e);
    	} catch (IllegalAccessException e) {
    		System.out.println("!! Illegal Access !!\n"+e);
    	} catch (InstantiationException e) {
    		System.out.println("!! Class Not Instanciaded !!\n"+e);
    	} finally {
    		if (st != null) {
    			try {
    				st.close();
    			} catch (Exception e) {
    				System.out.println("!! Exception returning statement !!\n"+e);
    			}
    		}
    		if (conn != null) {
    			try {
    				conn.close();
    			} catch (Exception e) {
    				System.out.println("!! Exception closing DB connection !!\n"+e);
    			}
    		}
    	} // end of finally
    	return p_id;
    }

    public boolean verStock(int aStock, int aID){
    	getJavaProperties();
    	try {
    		Class.forName("com.mysql.jdbc.Driver").newInstance();
    		conn = DriverManager.getConnection("jdbc:mysql://"+ip+":"+porto+"/"+nomebd, login, pass);
    		st = conn.createStatement();
    			
			rs = st.executeQuery("SELECT P_ID FROM produtos WHERE P_STOCK > '"+ aStock +"' AND P_ID = '"+ aID +"';");
    		if (rs == null) {
			  return false;
    		} else {
			  if (rs.next()) {
				  return true;
			  }
			}    		
    	
    		st.close();

    	} catch (SQLException e) {
    		System.out.println("!! SQL Exception !!\n"+e);
    		e.printStackTrace();
    	} catch (ClassNotFoundException e) {
    		System.out.println("!! Class Not Found. Unable to load Database Drive !!\n"+e);
    	} catch (IllegalAccessException e) {
    		System.out.println("!! Illegal Access !!\n"+e);
    	} catch (InstantiationException e) {
    		System.out.println("!! Class Not Instanciaded !!\n"+e);
    	} finally {
    		if (st != null) {
    			try {
    				st.close();
    			} catch (Exception e) {
    				System.out.println("!! Exception returning statement !!\n"+e);
    			}
    		}
    		if (conn != null) {
    			try {
    				conn.close();
    			} catch (Exception e) {
    				System.out.println("!! Exception closing DB connection !!\n"+e);
    			}
    		}
    	} // end of finally
    	return false;
    }
    
    /**
     * Este m�todo gera o n�mero aleat�rio SKU, que � �nico e vai de pode ir de 1 a 1 000 000.
     * @return - devolve o dito c�digo SKU.
     */
    public int geraSKU(){
    	int cod = 1 + (int)(Math.random() * 1000000);
    	do {
    		if(verSKU(cod)){
        		cod = 1 + (int)(Math.random() * 1000000);
        	} else {
        		break;
        	}
		} while (true);    	
    	return cod;
        
    }

    /**
     * Este m�todo � utilizado pelo m�todo a cima descrito, e serve para verificar se o n�mero gerado existe ou n�o. 
     * @param aSKU - Parametro dado pelo geraSKU()
     * @return - Devolve true caso exista, e false caso n�o exista.
     */
    
    public boolean verSKU(int aSKU){
    	getJavaProperties();
    	try {
    		Class.forName("com.mysql.jdbc.Driver").newInstance();
    		conn = DriverManager.getConnection("jdbc:mysql://"+ip+":"+porto+"/"+nomebd, login, pass);
    		st = conn.createStatement();
    			
			rs = st.executeQuery("SELECT * FROM produtos WHERE P_SKU = '"+ aSKU +"';");
    		if (rs == null) {
			  return false;
    		} else {
			  if (rs.next()) {
				  return true;
			  }
			}    		
    	
    		st.close();

    	} catch (SQLException e) {
    		System.out.println("!! SQL Exception !!\n"+e);
    		e.printStackTrace();
    	} catch (ClassNotFoundException e) {
    		System.out.println("!! Class Not Found. Unable to load Database Drive !!\n"+e);
    	} catch (IllegalAccessException e) {
    		System.out.println("!! Illegal Access !!\n"+e);
    	} catch (InstantiationException e) {
    		System.out.println("!! Class Not Instanciaded !!\n"+e);
    	} finally {
    		if (st != null) {
    			try {
    				st.close();
    			} catch (Exception e) {
    				System.out.println("!! Exception returning statement !!\n"+e);
    			}
    		}
    		if (conn != null) {
    			try {
    				conn.close();
    			} catch (Exception e) {
    				System.out.println("!! Exception closing DB connection !!\n"+e);
    			}
    		}
    	} // end of finally
    	return false;
    }
    
    /**
     * Este m�todo � utilizado para obter o custo de um produto. 
     * @param aID
     * @return 
     */
    
    public int getCusto(int aID){
    	
    	int custo = 0;
    	try {
    		Class.forName("com.mysql.jdbc.Driver").newInstance();
    		conn = DriverManager.getConnection("jdbc:mysql://"+ip+":"+porto+"/"+nomebd, login, pass);
    		
    		st = conn.createStatement();
    		
    		rs = st.executeQuery("SELECT P_PRECO FROM produtos WHERE P_ID = '"+ aID +"';");
    		
			rs.next();
			custo = rs.getInt("P_PRECO");
			    		
    	
    		st.close();

    	} catch (SQLException e) {
    		System.out.println("!! SQL Exception !!\n"+e);
    		e.printStackTrace();
    	} catch (ClassNotFoundException e) {
    		System.out.println("!! Class Not Found. Unable to load Database Drive !!\n"+e);
    	} catch (IllegalAccessException e) {
    		System.out.println("!! Illegal Access !!\n"+e);
    	} catch (InstantiationException e) {
    		System.out.println("!! Class Not Instanciaded !!\n"+e);
    	} finally {
    		if (st != null) {
    			try {
    				st.close();
    			} catch (Exception e) {
    				System.out.println("!! Exception returning statement !!\n"+e);
    			}
    		}
    		if (conn != null) {
    			try {
    				conn.close();
    			} catch (Exception e) {
    				System.out.println("!! Exception closing DB connection !!\n"+e);
    			}
    		}
    	} // end of finally
    	return custo;
    }
    
    /**
     * Este m�todo devolve uma string com uma pesquisa de um ou v�rios produtos
     * Dependendo dos parametros de entrada:
     * @param aTipo - tipo de pesquisa
     * @param aVar - argumento de pesquisa (exemplo: numa pesquisa por marca, este parametro tem a marca pela qual pesquisar)
     * @param aLogin - login do utilizador que est� a pesquisar, do cliente ou funcion�rio.
     * @return
     */
    public String pesquisaProduto(String aTipo, String aVar, String aLogin){
    	GereUtilizadores uti = new GereUtilizadores();
    	String str = "";
    	
    	if(aTipo == "designacao"){
    		str = "p_designacao like '%"+aVar+"%'";
    	}
    	if(aTipo == "categoria"){
    		str = "p_categoria like '%"+aVar+"%'";
    	}
    	if(aTipo == "stock"){
    		str = "p_stock < "+aVar;
    	}  	
    	if(uti.getTipoUti(aLogin).equals("Cliente")){
    		str += " and u_id = " + getUID(aLogin);
    	}
    	
    	getJavaProperties();
    	try {
    		Class.forName("com.mysql.jdbc.Driver").newInstance();
    		conn = DriverManager.getConnection("jdbc:mysql://"+ip+":"+porto+"/"+nomebd, login, pass);
    		
    		st = conn.createStatement();

    		rs = st.executeQuery("SELECT * " 
    							+ "FROM produtos "
    							+ "WHERE "+str+";");
    		String output = "";
    		String dataReparacao = "";
    		int i = 0;
    		while (rs.next()) {
    			i++;
    			if(rs.getString("e_datareparacao") == null || rs.getString("e_datareparacao").equals("")){
    				dataReparacao = "Ainda sem repara��o";
    			} else {
    				dataReparacao = rs.getString("e_datareparacao");
    			}
    			output += "\n        produto        \nMarca: " + rs.getString("e_marca")+"\nModelo: "  +rs.getString("e_modelo")+ "\nSetor Atividade: " 
    					+rs.getString("e_setoratividade")+ "\nData de Fabrico: " + rs.getString("e_datafabrico") + "\nLote: " +rs.getInt("e_lote")+"\nCodigo SKU: "  
    					+rs.getString("e_codigosku")+"\nData de Pedido: "  +rs.getString("e_datapedido")
    					+"\nData da Repara��o: "+dataReparacao+"\n";
    		if(i == 10){
				i=0;
    			output += "Limite de 10!";
    		}
    		}
    		if(output.equals("")){
    			output = "Nenhum produto encontrado com as especifica��es pedidas!";
    		}

			output += "Limite de 10!";
    		return output;

    	} catch (SQLException e) {
    		System.out.println("!! SQL Exception !!\n"+e);
    		e.printStackTrace();
    	} catch (ClassNotFoundException e) {
    		System.out.println("!! Class Not Found. Unable to load Database Drive !!\n"+e);
    	} catch (IllegalAccessException e) {
    		System.out.println("!! Illegal Access !!\n"+e);
    	} catch (InstantiationException e) {
    		System.out.println("!! Class Not Instanciaded !!\n"+e);
    	} finally {
    		if (st != null) {
    			try {
    				st.close();
    			} catch (Exception e) {
    				System.out.println("!! Exception returning statement !!\n"+e);
    			}
    		}
    		if (conn != null) {
    			try {
    				conn.close();
    			} catch (Exception e) {
    				System.out.println("!! Exception closing DB connection !!\n"+e);
    			}
    		}
    	} 
    	
    	return "";
    } 

    /**
     * Este m�todo verifica se existe algum produto ou n�o
     * Caso exista devolve true, caso contr�rio devolve false.
     * @return
     */
    public boolean exists(){
    	
    	getJavaProperties();
    	try {
    		Class.forName("com.mysql.jdbc.Driver").newInstance();
    		conn = DriverManager.getConnection("jdbc:mysql://"+ip+":"+porto+"/"+nomebd, login, pass);
    		st = conn.createStatement();
    			
			rs = st.executeQuery("SELECT count(*) FROM produtos;");
    		if (rs == null) {
			  return false;
    		} else {
			  while (rs.next()) {
				  int count = rs.getInt("count(*)");
				  if(count == 0){
					  return false;
				  } else {
					  return true;
				  }
			  }
			}    		
    	
    		st.close();

    	} catch (SQLException e) {
    		System.out.println("!! SQL Exception !!\n"+e);
    		e.printStackTrace();
    	} catch (ClassNotFoundException e) {
    		System.out.println("!! Class Not Found. Unable to load Database Drive !!\n"+e);
    	} catch (IllegalAccessException e) {
    		System.out.println("!! Illegal Access !!\n"+e);
    	} catch (InstantiationException e) {
    		System.out.println("!! Class Not Instanciaded !!\n"+e);
    	} finally {
    		if (st != null) {
    			try {
    				st.close();
    			} catch (Exception e) {
    				System.out.println("!! Exception returning statement !!\n"+e);
    			}
    		}
    		if (conn != null) {
    			try {
    				conn.close();
    			} catch (Exception e) {
    				System.out.println("!! Exception closing DB connection !!\n"+e);
    			}
    		}
    	}
    	
    	return false;
    }
    
    /**
	 * Este m�todo apenas carrega os dados de acesso a base de dados, para variaveis globais.
     */
    private void getJavaProperties(){

    	ip = file.leFicheiroTexto("ip");
    	porto = file.leFicheiroTexto("porto");
    	nomebd = file.leFicheiroTexto("nome");
    	login = file.leFicheiroTexto("login");
    	pass = file.leFicheiroTexto("pass");
    }
}
