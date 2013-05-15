## Projeto realizado no âmbito da unidade curricular "Projecto e Seminário", semestre de verão 2012/2013.

1. [Proposta de projeto](https://www.dropbox.com/s/larslc6nq7cau5f/pp32186.pdf)
1. [Relatório de progresso](https://www.dropbox.com/s/8gjpnj9l9v25uo4/rp32186.pdf)
1. [The Cloud Media Player](http://thecloudmediaplayer.herokuapp.com)


1. Configurações da aplicação
	1. Variáveis de ambiente
	
        Para evitar que alguns valores sejam disponibilizadas publicamente, mantendo a compatibilidade com o serviço *Heroku*, é necessário criar algumas variáveis de ambiente do sistema operativo:

		1. Utilizador da base de dados: ${PGUSER}
		1. _Password_ do utilizador da base de dados: ${PGPASSWORD}
		1. Chave secreta da aplicação: ${THE_CLOUD_MEDIA_PLAYER_APP_SECRET}

	1. 
