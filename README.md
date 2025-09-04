# Rome Transit Tracker (Damose)

#### Student Name: Diego Albani

#### Date: 17/09/2025

#### Features Implemented
```
● Funzionamento offline, con dati statici GTFS.

● Visualizzazione e ricerca delle fermate, che mostra le prossime linee che
  effettueranno la fermata e i relativi orari di arrivo.

● Visualizzazione e ricerca delle linee, che mostra la fermata corrente per
  ciascun veicolo della linea.

● Previsione dell'orario di arrivo di una linea con una sola fermata in base
  all'orario statico.

● Mappa di visualizzazione della posizione dei veicoli in base all'orario
  statico (non interattiva e senza aggiornamenti in tempo reale), che mostra
  il numero/codice della linea e la direzione del veicolo.

● Gestione differenziata delle diverse tipologie di veicoli (autobus, tram,
  ecc.).

● Aggiornamento in tempo reale della posizione dei bus (se online), sia sulla
  mappa che nei risultati di ricerca.

● Possibilità di salvare preferiti (linee e fermate).

● Predizioni di arrivo usando i dati in tempo reale.

● Switch automatico tra online e offline.

● Autenticazione utente e gestione personalizzata dei preferiti.

● Testing unitario per validare il funzionamento del sistema.
```

## 1. Project Overview

```
● Project Name: Rome Transit Tracker
● Core Problem Solved: Applicazione desktop che consente la consultazione e l’esplorazione
di dati di trasporto pubblico di Roma. Gestisce linee, fermate e orari, combinando static
GTFS data con aggiornamenti realtime (quando disponibili) e offrendo funzioni di ricerca,
visualizzazione su mappa e gestione preferiti.
```
## 2. Object-Oriented Design (OOD) Decisions

### 2.1 Class Design & Responsibilities
```
1. GTFSManager
    ● Primary Responsibility: Gestire il caricamento e l’accesso a static GTFS data.
    ● Justification: Mantiene in memoria tabelle GTFS e fornisce metodi di filtro (es.
       service_id). Incapsula l’accesso ai dati evitando ridondanza nel resto dell’app.
2. ModelManager
    ● Primary Responsibility: Centralizzare il modello dati applicativo (Route, Stop, Trip,
       StopTime).
    ● Justification: Fornisce metodi per popolare e interrogare il modello (filtri, ordini,
       prossimi arrivi), isolando la logica dai controllers.
3. UIEventController
    ● Primary Responsibility: Collegare la view principale con i vari controllers gestendo
       eventi di input, interazioni e aggiornamenti della UI.
    ● Justification: Coordina interfaccia e logica (es. doppi click, ricerche, pulsanti)
       mantenendo separate la presentazione e la business logic.
4. MapController
    ● Primary Responsibility: Gestire la visualizzazione su mappa di stops, bus e ping
       waypoint.
    ● Justification: Centralizza tutte le operazioni su JXMapViewer (aggiunta/rimozione
       waypoint, routing data, colori) rendendo il codice riusabile e isolato.
5. AuthenticationManager
    ● Primary Responsibility: Gestire registrazione e login tramite FileUserRepository.
    ● Justification: Incapsula la logica di autenticazione dietro l’interfaccia
       AuthenticationService, permettendo eventuali sostituzioni della persistenza.
```

### 2.2 Encapsulation

```
● Tutti gli attributi delle classi sono dichiarati private o protected.
● L’accesso avviene attraverso metodi pubblici (getters, setters o metodi dedicati).
● Benefici: impedisce manipolazioni dirette dello stato interno, riduce coupling e rende il codice
più mantenibile.
```
### 2.3 Inheritance

```
● Esempi:
● MyWaypoint estende DefaultWaypoint.
● ButtonWaypoint estende JButton.
● WaypointRender estende WaypointPainter.
● L’uso dell’inheritance permette di riutilizzare funzionalità base delle classi Swing/jxmapviewer
e aggiungere comportamenti specifici (es. rendering personalizzato) con minore duplicazione
rispetto a composition pura.
```
### 2.4 Polymorphism

```
● AuthenticationService è un’interfaccia implementata da AuthenticationManager,
permettendo di invocare login() e register() senza conoscere la classe concreta.
● Override dei metodi in classi derivate (es. WaypointRender.paint() rispetto a
WaypointPainter).
```
### 2.5 Abstraction

```
● L’interfaccia AuthenticationService espone operazioni d’accesso senza rivelare i dettagli di
persistenza (file, db, ecc.), concentrandosi sulle funzioni essenziali.
● I controllers interagiscono con la view tramite metodi pubblici senza conoscere i componenti
interni, mantenendo la view astratta rispetto alla logica.
```
### 2.6 Design Patterns

```
● Singleton: GTFSManager, ModelManager e FavoritesManager sono singletons, garantendo
un’unica istanza condivisa.
● MVC/MVP style: suddivisione in controller, model e view, che facilita la separazione delle
responsabilità.
```

## 3. Architectural & Project Management Considerations

### 3.1 Scalability

```
● Struttura modulare con controllers separati (LineController, StopController, BusController,
ecc.) e managers (ModelManager, GTFSManager).
● È possibile aggiungere nuove features (es. altre sorgenti dati, differenti views).
● Separazione data logic / UI logic semplifica la futura integrazione di nuove interfacce o di un
database.
```
### 3.2 Maintainability

```
● Nomenclatura chiara e commenti estesi in italiano.
● Suddivisione in classi piccole e focalizzate (responsabilità singola).
● Uso coerente di getter/setter, costruttori dedicati e metodi statici di utilità (es.ConnectivityUtil).
● Nuovi sviluppatori possono orientarsi grazie alla struttura MVC e ai commenti.
```
### 3.3 External libraries

**JXMapViewer**
```
● Utilizzata per la visualizzazione della mappa, la gestione dei tile, dei waypoint e delle
interazioni di navigazione.
● Classi importate: JXMapViewer, OSMTileFactoryInfo, DefaultTileFactory, GeoPosition,
WaypointPainter, PanMouseInputListener, ZoomMouseWheelListenerCenter,
FileBasedLocalCache, ecc.
● Presente in varie componenti grafiche, ad esempio:
● MapView per configurare la mappa e gestire cache e zoom
● MainView per integrare il componente mappa nell’interfaccia utente principale
● MyWaypoint per estendere i waypoint mostrati sulla mappa
```

**Google GTFS Realtime**
```
● Utilizzata per il parsing e l’interpretazione dei feed GTFS in tempo reale forniti da Roma
Mobilità.
● Classi importate: GtfsRealtime.FeedMessage, GtfsRealtime.TripUpdate,
GtfsRealtime.TripDescriptor, ecc.
● Impiegata in:
● GTFSFetcher per scaricare e interpretare i messaggi GTFS realtime e aggiornare le
posizioni dei mezzi e gli orari delle corse
● Trip per rappresentare e aggiornare i viaggi con dati realtime tramite
TripDescriptor
```

### Conclusion

Il progetto Rome Transit Tracker adotta un approccio orientato agli oggetti con attenzione alla
separazione delle responsabilità. La combinazione di GTFSManager/ModelManager per i dati,
controllers specializzati per la logica e un’interfaccia grafica Swing ne fanno un’applicazione
modulare e relativamente facile da estendere. L’uso di pattern come Singleton e di un’interfaccia di
autenticazione migliora riusabilità e testabilità. La base consente l’evoluzione verso funzionalità
avanzate (update realtime completo, mappe interattive, supporto multi‑utente) mantenendo un
impianto scalabile e mantenibile.
