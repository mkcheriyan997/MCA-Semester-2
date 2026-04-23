const fs = require('fs');
const { mdToPdf } = require('md-to-pdf');
const path = require('path');

function generateTOC(content) {
    const lines = content.split('\n');
    let toc = '## Table of Contents\n\n';
    let hasHeadings = false;
    
    for (const line of lines) {
        if (line.startsWith('## ') || line.startsWith('### ')) {
            if (line.includes('Table of Contents')) continue; // Skip TOC itself
            
            const isSub = line.startsWith('### ');
            const title = line.replace(/^#{2,3}\s+/, '').trim();
            // Generate basic github-style anchor
            const anchor = title.toLowerCase().replace(/[^a-z0-9]+/g, '-').replace(/(^-|-$)/g, '');
            
            toc += `${isSub ? '  -' : '-'} [${title}](#${anchor})\n`;
            hasHeadings = true;
        }
    }
    
    return hasHeadings ? toc + '\n---\n\n' : '';
}

(async () => {
    try {
        console.log("Starting PDF generation with TOC and Border...");
        let content = fs.readFileSync('LifeLoad_Project_Report.md', 'utf-8');
        
        // Find insert point for TOC (after '## 1. Introduction' or just after the first '---')
        const insertMatch = content.match(/---\n/);
        if (insertMatch) {
            const index = insertMatch.index + insertMatch[0].length;
            const toc = generateTOC(content);
            content = content.substring(0, index) + '\n' + toc + content.substring(index);
        }
        
        // Find all images and replace with base64 for reliable rendering
        const imgRegex = /!\[(.*?)\]\((.*?)\)/g;
        content = content.replace(imgRegex, (match, alt, imgPath) => {
            try {
                if (imgPath.startsWith('/home') && fs.existsSync(imgPath)) {
                    const ext = path.extname(imgPath).substring(1) || 'png';
                    const base64 = fs.readFileSync(imgPath, 'base64');
                    return `![${alt}](data:image/${ext};base64,${base64})`;
                }
            } catch (err) {
                console.error("Failed to read image:", imgPath);
            }
            return match;
        });
        
        await mdToPdf({ content }, { 
            dest: 'LifeLoad_Project_Report.pdf',
            launch_options: { args: ['--no-sandbox', '--disable-setuid-sandbox', '--allow-file-access-from-files'] },
            css: `
                body {
                    border: 4px solid #1a202c;
                    border-radius: 8px;
                    padding: 40px;
                    margin: 0;
                    box-sizing: border-box;
                    min-height: 100%;
                }
                .markdown-body {
                    font-family: -apple-system,BlinkMacSystemFont,Segoe UI,Helvetica,Arial,sans-serif;
                }
            `,
            pdf_options: { 
                format: 'A4', 
                margin: { top: '15mm', bottom: '15mm', left: '15mm', right: '15mm' } 
            }
        });
        
        console.log("PDF generated successfully with TOC and border!");
    } catch (error) {
        console.error("Error generating PDF:", error);
    }
})();
