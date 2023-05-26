// Définition de la fonction qui affiche "Hello, World!"
//function afficherHelloWorld() {
//    console.log("Hello, World!");
//  }
  
  // Appel de la fonction pour afficher "Hello, World!"
  //afficherHelloWorld();
  import { jsPDF } from "jspdf";

  // Default export is a4 paper, portrait, using millimeters for units
  const doc = new jsPDF();
  
  doc.text("Hello world!", 10, 10);
  doc.save("a4.pdf");