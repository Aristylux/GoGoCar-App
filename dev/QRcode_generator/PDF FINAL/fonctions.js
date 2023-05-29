var qr_liste = [];
    
        function ajouterElement(value) {
            var qr_liste_ul = document.getElementById("qr_liste");
            var qr_liste_li = document.createElement("li");
            qr_liste_li.textContent = value;
            qr_liste_ul.appendChild(qr_liste_li);
            qr_liste.push(value);
            //addQRCodeToTable(qr_liste.length - 1);
        }
    
        function addQRCodeToTable(i) {
            var qr_tableau_div = document.getElementById("qr_tableau");
            var tbody = qr_tableau_div.querySelector("#qr_tableau tbody");//"#qr_tableau tbody" plus précis que "tbody"

            // Create elements for the row
            var row = document.createElement("tr");
            var td1 = document.createElement("td");
            var td2 = document.createElement("td");
            var tdElement = document.createElement("td");
            
            // Create div for the QR code and append to the tdElement
            var qr_code_div = document.createElement("div");
            tdElement.appendChild(qr_code_div);

            // Set content for the other cells
            td1.textContent = (i+1);
            td2.textContent = qr_liste[i];

            // Append cells to the row
            row.appendChild(td1);
            row.appendChild(tdElement);
            row.appendChild(td2);

            // Append row to the tbody
            tbody.appendChild(row);

            // Generate QR code after the div is appended to the DOM
            new QRCode(qr_code_div, qr_liste[i]);

        }

        
        function generatePdf(){
            const { jsPDF } = window.jspdf; //ici j'importe jspdf de la librairie jspdf.js et j'utilise window car j'utilise le chargement dans la page web
            // Créer une instance de jsPDF
            //import { jsPDF } from "jspdf";
            //const { jsPDF } = require("jspdf"); //ici j'importe un fichier javascript comme s'il était dans un module, sans le créer 
            const doc = new   jsPDF();
            
            // Sélectionner tous les éléments contenant les images QR code
            const qrCodeDivs = document.querySelectorAll('#qr_tableau div');
            
            // Parcourir tous les éléments et générer les images QR code dans le document PDF
            qrCodeDivs.forEach(function(qrCodeDiv, index) {
                // Obtenir les dimensions de l'image
                const imgWidth = qrCodeDiv.offsetWidth;
                const imgHeight = qrCodeDiv.offsetHeight;
                
                // Créer une représentation de l'image QR code en tant que base64
                const imageData = qrCodeDiv.querySelector('img').src;
                
                // Dessiner l'image sur le document PDF
                if (index !== 0) {
                    doc.addPage(); // Ajouter une nouvelle page pour chaque image QR code sauf la première
                }
                doc.addImage(imageData, 'JPEG', 5, 5, 200, 200);

            });
            
            // Enregistrer le fichier PDF
            doc.save('qr-codes.pdf');
        }
        const but_generate_pdf = document.getElementById('generate-pdf');

        but_generate_pdf.addEventListener('click', function() {
            generatePdf(); 
        });

        function genererTableau() {
            var qr_tableau_div = document.getElementById("qr_tableau");
            qr_tableau_div.innerHTML = "<table><thead><tr><th>#</th><th>Code QR</th><th>Texte</th></tr></thead><tbody></tbody></table>";

            for (var i = 0; i < qr_liste.length; i++) {
                addQRCodeToTable(i);
            }
        }