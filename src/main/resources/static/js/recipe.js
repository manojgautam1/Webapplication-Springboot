document.addEventListener('DOMContentLoaded', function() {
    function downloadModalContentAsPDF(index) {

        if (typeof window.jspdf === 'undefined' || !window.jspdf.jsPDF) {
            alert('PDF library is still loading. Please try again in a moment.');
            return;
        }

        const modal = document.getElementById('modal-' + index);
        if (!modal) {
            console.error('Modal not found for index:', index);
            return;
        }

        try {
            const { jsPDF } = window.jspdf;
            const doc = new jsPDF();

            const pageWidth = doc.internal.pageSize.width;
            const pageHeight = doc.internal.pageSize.height;
            const margin = 20;
            const maxWidth = pageWidth - (margin * 2);
            let yPosition = margin;

            const primaryColor = [52, 138, 81];
            const secondaryColor = [100, 100, 100];
            const textColor = [40, 40, 40];

            function checkPageBreak(neededHeight) {
                if (yPosition + neededHeight > pageHeight - margin) {
                    doc.addPage();
                    yPosition = margin;
                    return true;
                }
                return false;
            }

            function addStyledText(text, fontSize, color, isBold = false, indent = 0) {
                doc.setFontSize(fontSize);
                doc.setTextColor(color[0], color[1], color[2]);
                doc.setFont('helvetica', isBold ? 'bold' : 'normal');

                const lines = doc.splitTextToSize(text, maxWidth - indent);
                const lineHeight = fontSize * 0.4;

                checkPageBreak(lineHeight);

                lines.forEach((line, index) => {
                    if (yPosition + lineHeight > pageHeight - margin - 20) {
                        doc.addPage();
                        yPosition = margin;
                    }

                    doc.text(line, margin + indent, yPosition);
                    yPosition += lineHeight;
                });

                return lines.length * lineHeight;
            }
            function addSection(title, items, itemPrefix = '• ') {

                addStyledText(title, 14, primaryColor, true);
                yPosition += 5;
                items.forEach((item, index) => {
                    const itemText = `${itemPrefix}${item}`;
                    addStyledText(itemText, 11, textColor, false, 10);
                    yPosition += 2;
                });

                yPosition += 10;
            }
            const title = modal.querySelector('.modal-title')?.innerText || 'Recipe';
            const modalBody = modal.querySelector('.modal-body');

            const ingredients = modalBody.querySelector('p:nth-child(1) span')?.innerText || 'Not available';
            const calories = modalBody.querySelector('p:nth-child(2) span')?.innerText || 'Not available';
            const directions = modalBody.querySelector('pre')?.innerText || 'Not available';
            const protein = modalBody.querySelector('p:nth-child(4) span')?.innerText || 'Not available';
            const fat = modalBody.querySelector('p:nth-child(5) span')?.innerText || 'Not available';
            const carbs = modalBody.querySelector('p:nth-child(6) span')?.innerText || 'Not available';
            const fiber = modalBody.querySelector('p:nth-child(7) span')?.innerText || 'Not available';

            const vitaminElements = modalBody.querySelectorAll('ul li');
            let vitamins = [];
            vitaminElements.forEach(li => {
                const vitaminText = li.innerText.trim();
                if (vitaminText && vitaminText !== 'No vitamin data available') {
                    vitamins.push(vitaminText);
                }
            });

            doc.setFillColor(primaryColor[0], primaryColor[1], primaryColor[2]);
            doc.rect(0, 0, pageWidth, 40, 'F');

            doc.setTextColor(255, 255, 255);
            doc.setFontSize(20);
            doc.setFont('helvetica', 'bold');
            doc.text(title, margin, 25);

            yPosition = 60;

            // Nutritional Overview Box
            doc.setFillColor(245, 245, 245);
            doc.rect(margin, yPosition - 10, maxWidth, 35, 'F');
            doc.setDrawColor(primaryColor[0], primaryColor[1], primaryColor[2]);
            doc.rect(margin, yPosition - 10, maxWidth, 35);

            doc.setTextColor(textColor[0], textColor[1], textColor[2]);
            doc.setFontSize(12);
            doc.setFont('helvetica', 'bold');
            doc.text('Nutritional Information', margin + 5, yPosition);

            doc.setFont('helvetica', 'normal');
            doc.setFontSize(10);
            doc.text(`Calories: ${calories} kcal  |  Protein: ${protein}g  |  Fat: ${fat}g  |  Carbs: ${carbs}g  |  Fiber: ${fiber}g`, margin + 5, yPosition + 15);

            yPosition += 50;

            // Ingredients Section
            addStyledText('INGREDIENTS NEEDED AND STEPS', 14, primaryColor, true);
            yPosition += 10;

            // Format ingredients nicely
            const ingredientsList = ingredients.split(',').map(ing => ing.trim()).filter(ing => ing);
            addSection(' INGREDIENTS', ingredientsList);


            // Directions Section
            const cleanDirections = directions.replace(/\s+/g, ' ').trim();
            const sentences = cleanDirections.split(/(?<=[.!?])\s+/).filter(s => s.trim());

            sentences.forEach((sentence, index) => {
                const stepText = `${index + 1}. ${sentence.trim()}`;
                addStyledText(stepText, 11, textColor, false, 10);
                yPosition += 5; // Space between steps
            });
            yPosition +=10;

            if (vitamins.length > 0) {
                addStyledText(' VITAMINS & MINERALS', 14, primaryColor, true);
                yPosition += 10;

                vitamins.forEach(vitamin => {
                    yPosition += addStyledText(`• ${vitamin}`, 11, textColor, false, 10) + 3;
                });
            }

            // Footer
            const footerY = pageHeight - 20;
            doc.setFontSize(8);
            doc.setTextColor(150, 150, 150);
            doc.text('Generated by Recipe App', margin, footerY);
            doc.text(new Date().toLocaleDateString(), pageWidth - margin - 30, footerY);

            // Save the PDF
            const filename = title.replace(/[^a-zA-Z0-9]/g, '_').toLowerCase() + '_recipe.pdf';
            doc.save(filename);

        } catch (error) {
            console.error('Error generating PDF:', error);
            alert('Failed to generate PDF. Please try again.');
        }
    }
    // Make function globally available
    window.downloadModalContentAsPDF = downloadModalContentAsPDF;

    // Add event listeners to download buttons
    document.querySelectorAll('button[onclick*="downloadModalContentAsPDF"]').forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            const onclick = this.getAttribute('onclick');
            const indexMatch = onclick.match(/\d+/);
            if (indexMatch) {
                const index = parseInt(indexMatch[0]);
                downloadModalContentAsPDF(index);
            }
        });
    });
});