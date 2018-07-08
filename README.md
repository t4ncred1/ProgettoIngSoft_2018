# Progetto di Ingegneria del Software A.A. 2017/18
## Collaboratori
Costanzo Andrea, codice persona 10497583  
Cricri  Giuseppe, codice persona 10495400  
Covioli Tancredi, codice persona 10498705  

## Preconfigurazione del sistema
A causa di problemi di visualizzazione dei colori nella console standard, per gli utenti windows abbiamo incluso all'interno della cartella _deliverables_ il tool **ansicon**, nelle sue versioni a 32 bit (_ansicon32.exe_) e a 64 bit (_ansicon64.exe_).
Il lavoro svolto dal tool in questione consiste nel convertire gli _escape charachters_ per la colorazione dell'output dal formato ansi a un formato windows friendly.
Purtroppo, é risultato funzionante solo su _cmd_, di conseguenza ci troviamo costretti a sconsigliare l'utilizzo di _powershell_.

Chiediamo pertanto a coloro che decidessero di usare il sistema operativo in questione <sup>[1](#windowsFaSchifo)</sup> , se fosse possibile, di avviare uno di questi due file da riga di comando prima di procedere :
* `ansicon32` per sistemi operativi a 32 bit.
* `ansicon64` per sistemi operativi a 64 bit.

## Informazioni  sulla configurazione di gioco
Per l'avvio standard di Sagrada, è sufficiente l'avvio dei file .jar (forniti in deliverables) tramite console:

`java -jar `__*`path_to_jar_file`*__` `

Per configurare la propria esperienza di gioco, possono essere modificati i file di configurazione _clientconfig.json_ e _config.json_ (contenuti in deliverables\resources).



---
<a name="windowsFaSchifo"><sup>1</sup></a>: _(scelta, almeno per opinione personale, discutibile) --Covioli Tancredi_
