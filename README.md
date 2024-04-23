1.	Introducere 
1.1	Scop 
Scopul acestui proiect este de a face mai ușoara găsirea prietenilor în orice oraș/țară folosind GPS-ul de la Google Maps. De asemenea, puteți crea un profil de utilizator și interactiona cu oameni noi. 
1.2 Domeniul de aplicare al acestui produs 
Domeniul de aplocare al acestui proiect este de a conecta oamenii indiferent de locul în care se află. Aplicația permite utilizatorilor să:
•	Isi gaseasca prietenii
•	Se imprieteneasca cu oameni noi
•	Schimbe locațiile
2.	Descriere generală 
2.1 Perspectiva produsului 
Acest proiect a fost creat cu ideea unui produs auto-suficient care ajută oamenii să-și găsească prietenii prin intermediul unui Sistem de Poziționare Globală. Utilizatorii pot, de asemenea, să adauge în lista lor de prieteni oameni noi sau sa elimine din lista anumiti prieteni. 
2.2 Funcții ale produsului
Versiunea finală a produsului va avea următoarele caracteristici:
•	înregistrarea utilizatorului
•	autentificarea utilizatorului
•	ieșirea utilizatorului din cont
•	funcția de căutare pentru utilizatori
•	date personale despre utilizator
•	adăugarea de prieteni
•	ștergerea prietenilor
•	găsirea locației unui prieten

 2.3 Clase și caracteristici ale utilizatorilor 
Utilizatorii aplicației vor avea următoarele drepturi:
•	pot accesa aplicația creând un cont
•	pot accesa Google Maps
•	pot vizualiza locația prietenilor lor
•	pot căuta și adăuga/șterge prieteni. 
2.4 Mediu de operare 
Cerința minimă a utilizatorului este: Android 11.0. Aplicația rulează atât pe dispozitive fizice, cât și pe cele virtuale. 
3.	Cerințe de interfață externe 
3.1 Interfețe utilizator 
Activitatea Principală 
Când aplicația este lansată, utilizatorii trebuie să se autentifice în aplicație (dacă contul este creat) sau să se înregistreze. Pentru autentificare și înregistrare am creat 2 butoane, fiecare buton ne duce către o pagină specifică.
Poza butoane REGISTER si LOGIN
Activitatea de Înregistrare 
În primul rând, dacă un utilizator nou al aplicației dorește să o acceseze, trebuie să-și creeze un cont în aplicație folosind pagina de Înregistrare. Următorul pas este realizat de aplicație însăși, salvând datele, după care utilizatorul trebuie să acceseze pagina de Autentificare pentru a intra în aplicație.
 

Activitatea de Autentificare 
Pentru a vă autentifica în aplicație, utilizatorii trebuie să introducă datele lor personale corespunzătoare conturilor lor. Următorul pas este realizat de aplicație însăși, care recunoaște dacă numele de utilizator și parola corespund unui utilizator.
 

Activitatea Hartă 
După ce au finalizat cu succes autentificarea, utilizatorii aplicației pot vizualiza locația lor în timp real pe hartă. După adăugarea prietenilor, utilizatorii vor putea partaja locația, astfel încât să poată vedea atât propria locație, cât și locația prietenilor lor.
Poza HARTA
Activitatea Profilului Utilizatorului 
Utilizatorii pot vizualiza propriile profiluri și isi pot modifica datele.
Poza Profil
Activitatea Găsirea Prietenilor 
Interfața cea mai importantă a aplicației la care utilizatorii pot accesa este adăugarea/ștergerea prietenilor folosindu-și e-mail-ul personal/numărul de telefon.
Poza Gasire Prieteni
4.	Caracteristici ale Sistemului 
În acest paragraf sunt notate și descrise cerințele funcționale pentru aplicație. 
4.1 Autentificare Utilizator 
4.1.1 Descriere și Prioritate 
Utilizatorul va putea să se autentifice în aplicație folosind contul său specific, pe care aplicația îl va recunoaște.
 Prioritate: Ridicată 
4.1.2 Secvențe Stimul-Răspuns 
Intrare: Pe pagina de înregistrare, următoarele informații vor fi completate: parola și numele de utilizator specific, apoi se apasă butonul de Autentificare. 
Ieșire: Starea de autentificare. 
4.1.3 Cerințe Funcționale
•	Utilizatorul va introduce numele de utilizator și parola.
•	Utilizatorul va apăsa butonul de autentificare. 
REQ-23: Este necesar un câmp de text în care utilizatorul introduce numele de utilizator. 
REQ-24: Este necesar un câmp de text în care utilizatorul introduce parola.
REQ-25: Este necesar un buton pe care utilizatorul trebuie să-l apese după introducerea datelor. 
REQ-26: Dacă utilizatorul introdus este greșit, atunci autentificarea nu poate fi realizată cu succes și va apărea un mesaj de eroare. 
REQ-27: Dacă parola introdusă nu corespunde utilizatorului, atunci acesta nu va putea să se autentifice cu succes și va apărea un mesaj de eroare. 
REQ-28: Dacă numele de utilizator și parola sunt introduse corect, utilizatorul va intra în Activitatea Hartă. 
4.2 Adăugați-vă Prietenii 
4.2.1 Descriere și Prioritate 
Pe pagina Găsiți-vă prietenii, utilizatorul poate adăuga noi prieteni apăsând unul dintre butoanele Adăugare prieteni, urmând deschiderea unei noi pagini unde utilizatorul va completa datele specifice tipului de adăugare selectat. 
Prioritate: Ridicată 
4.2.2 Secvențe Stimul-Răspuns 
Intrare1: Există un buton de Adăugare pe pagina Găsiți-mi prietenii. 
Ieșire1: Butonul va deschide un câmp pentru introducerea e-mail-ului/numărului de telefon. 
Intrare2: Apăsați butonul de adăugare.
 Ieșire2: Salvează datele introduse. 
4.2.3 Cerințe Funcționale 
REQ-14: Pe pagina Găsiți-mi prietenii există doua butoane pentru adăugarea persoanelor în lista de prieteni, unde utilizatorul va selecta butonul specific. 
REQ-15: După completarea câmpului, utilizatorul va apăsa butonul de Adăugare care va trimite persoanei pe care doreste să o adauge, un mesaj în care aceasta trebuie să accepte sau să refuze.
