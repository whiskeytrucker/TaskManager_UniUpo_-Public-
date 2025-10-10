# Task Manager (App Mobili)
## TODO
PRINCIPALI
- [X] Progetti
	- [X] Task
		- [X] Sottotask
- [X] Notifiche
- [X] Chat



FUN:
- [X] Login / Logout / Registrazione e relativi tasti
	- [X] Controllo input mail + password NO vuoto etc.
- [X] Autenticazione e separazioni utenti (PM, PL, D, e il cristo di dio)
- [X] Separazioni utenti (pescare dal db utente <--> tipo e segnare su currentUser)
- [X] Un **task** e sui relativi **sottotask** possono essere: **aggiunti/eliminati/aggiornati**
	- [X] Un **task** può essere modificato solo da un PL
	- [X] Un **sottotask** può essere modificato sia da un PL che da un D
	- [X] AGGIUNGERE LA AGGIUNTA/MODIFICA DI "PROGRESS"
		- Aggiornabile su modifica
	
	RICERCA
	- [X] Il **PM** può fare ricerche sui progetti mettendo filtri in base allo **stato**, al **PL** e alla ~~scadenza~~.
		- Per **stato** penso intenda se il progetto è sopra a una certa percentuale (Progetti > o < di 50%)
	- [X] I **PL** possono fare qualcosa di analogo ma relativo ai **task**, quindi filtrando per **stato**, **D**, **scadenza** o **priorità**
		- Filtro su **stato** analogo alla ricerca sui progetti
		- Filtro sulla **scadenza** in base alla giornata di oggi + 1 mese & oggi + 3 mesi
		- Filtro sulla **priorità**, boh
		
	NOTIFICHE
	- [X] **D** modifica progess di una sottotask, se questo completa una task --> Notifica verso il **PL**.
	- [X] Progetto completato --> Notifica al **PM**.
	- [X] Sollecito **PL** --> **D**  ||  **PM** --> **PL**.
	
	CHAT
	- [X] Videata
	- [X] Mandare messaggi
	- [X] Ricevere messaggi instant
	
	RATING
	- [ ] Ad ogni **sottotask** è possibile assegnare una **foto/documento** relativa/o al task (tipo esempio dell'interfaccia grafica che si è sviluppato)
	- [ ] **Voto** e **scrivere un commento** su un lavoro svolto.
	
	

VIEW:
- [X] Menù laterale  E videata con lista progetti
- [X] Lista Task una volta clickato un progetto
	- [X] Mostrare SOLO Lista Task se Dev
- [X] Lista Sotto Task una volta clickata una Task
- [ ] Tasto indietro per tornare alla "pagina" precedente
	- [ ] Task --> Progetti OK | Sottotask --> Task Errore

- [X] Pagina Utente -- Giusto da visualizzare il tipo di utente





## BUG
- [X] Videata progetti non si aggiorna quando si fa logout/login
- [X] Tasto Home crasha quando cliccato dentro la Task
- [X] Refreshare Task e Subtask ogni volta che si effettua la modifica
	- [X] Tasti Modifica e Cancella scompaiono poiché si "dimentica" del tipo utente
- [X] Refreshare Task e Subtask ogni volta che si effettua la cancellazione
- [X] Duplicazione messaggio in Chat
- [x] ID Notifica tiene salvata quella vecchia --> Messi Flag corretti in PendingIntent
- [ ] Aggiornare domain/company name del pacchetto
- [ ] Aggiustare inversione chat:
	- Esiste chat da A a B, ma io sono B e non posso accedere alla chat: B-A Chat = !(A-B Chat)


## Testo Progetto
**Progetto Android**  

Il progetto riguarda la realizzazione di una app per la gestione dei task in una azienda con un
- **Projects Manager (PM)** incaricato di assegnare progetti ai **Project Leader** (PL) e monitorarne lo stato di avanzamento
- il PL deve assegnare i task del progetto ai vari **Developers** (D) e monitorane lo stato di avanzamento

Un task corrisponde a un lavoro di implementazione che uno sviluppatore deve compiere ed è composto da un **nome**, una **descrizione**, lo **sviluppatore** a cui è stato assegnato ed i suoi **sottotask**. 
Per ogni **sottotask** è assegnato una **scadenza**, una **priorità** e uno **stato** (TODO, assigned, completed) e, **in caso di "assigned"** anche l'avanzamento dei lavori espresso in **percentuale**.
Anche il **task** ha una **scadenza** e un **valore percentuale dell'avanzamento** che è la **media degli avanzamenti dei sottotask**. In maniera analoga, l'avanzamento del progetto è la media delle percentuali dei vari task.

Il **PL** in ogni momento può vedere lo **stato di avanzamento** dei singoli **sottotask** dei vari **D**, il **PM** può vedere lo stato di avanzamento del progetto.
**Il sistema di notifiche** prevede un meccanismo a cascata:
- **D** in ogni momento può decidere che **un sottotask ha avuto un incremento di lavoro**, se questo incremento porta al **completamento dell'intero task**, deve partire una notifica verso il PL.
- Quando un progetto è **completato** (tutti i task a lui assegnati sono stati completati) una **notifica** viene inviata al **PM**.
- Quando avviene un **sollecito** da parte del **PL** nei confronti di un **D** o da parte del **PM** nei confronti del **PL**.

Un **task** e sui relativi **sottotask** possono essere: **aggiunti/eliminati/aggiornati**.
Le **chat** tra i vari attori sono possibili secondo questo schema:
- D e D dello stesso progetto
- PL e D
- PM e PL

Con lo **stesso schema** sopra è possibile dare un **voto** e **scrivere un commento** su un lavoro svolto.
Ad ogni **sottotask** è possibile assegnare una **foto/documento** relativa/o al task (tipo esempio dell'interfaccia grafica che si è sviluppato)
Il **PM** può fare ricerche sui progetti mettendo filtri in base allo **stato**, al **PL** e alla **scadenza**.
I **PL** possono fare qualcosa di analogo ma relativo ai **task**, quindi filtrando per **stato**, **D**, **scadenza** o **priorità**.


## Autore
[@whiskeytrucker](https://github.com/whiskeytrucker)
