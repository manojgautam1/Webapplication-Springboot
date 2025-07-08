function openBlog(id) {
    document.getElementById(`blogModal${id}`).classList.add("active");
    document.body.style.overflow = "hidden";
}

function closeBlog(id) {
    document.getElementById(`blogModal${id}`).classList.remove("active");
    document.body.style.overflow = "auto";
}

// Close modal when clicking outside
window.onclick = function (event) {
    if (event.target.classList.contains("modal")) {
        event.target.classList.remove("active");
        document.body.style.overflow = "auto";
    }
};

//Blog card

const modal = document.getElementById("blogModal");
const modalTitle = document.getElementById("modalTitle");
const modalText = document.getElementById("modalText");
const modalImage = document.getElementById("modalImage");

const blogContent = {
    "future-eshop": {
        title: "The Future of E-commerce in Organic Food",
        content: `<p>As sustainability and health consciousness continue to rise, the demand for organic food e-shops is growing, reshaping how we source, purchase, and consume fresh, chemical-free products.</p>
                   <p>By connecting consumers directly with organic farmers and producers through digital platforms, these e-shops are eliminating geographical barriers and ensuring that high-quality, sustainable food reaches doorsteps, no matter the location. This direct-to-consumer model is revolutionizing the organic food market, fostering a closer relationship between growers and buyers.</p>
                   <h3>Key Innovations</h3>
                   <p>Recent advancements in organic food e-commerce include:</p>
                   <ul>
                       <li>Biodegradable materials reduce environmental impact</li>
                       <li>Advanced refrigeration keeps organic produce fresh during delivery.</li>
                       <li>Smart ordering and searching</li>
                       <li>Machine learning tailors product recommendations for users</li>
                   </ul>
                   <p>These innovations are making online purchase more convenient and efficient than ever before, paving the way for widespread adoption of electronic medium for convenient.</p>`,
    },
    "organic-tips": {
        title: "Tips for Organic Farming",
        content: `<p>Enhancing your organic farm’s productivity while maintaining sustainability is key to success. Here are some expert tips to help you thrive in organic farming.</p>

                    <h3>Best Practices</h3> 
                    <p>1. Rotate crops seasonally to maintain soil health and prevent pest buildup.</p> 
                    <p>2. Use natural compost and manure to enrich soil fertility.</p> 
                    <p>3. Implement companion planting to naturally deter pests and boost growth.</p> 
                    <p>4. Conserve water with drip irrigation and mulching techniques.</p> 
                    <h3>Common Mistakes to Avoid</h3> 
                    <p>Understand the practices that could harm your organic farm, such as over-tilling or neglecting soil testing, and learn how to avoid them. Consistent care and sustainable methods can ensure long-term success for your farm.</p>`
    },
    "impact-innovation": {
        title: "Sustainability and Integration of Organic Farming with Innovation",

        content: `<p>Organic farming, when integrated with innovative technologies, is paving the way for a more sustainable agricultural ecosystem. By leveraging modern tools and practices, we’re reducing the environmental impact of farming while ensuring long-term productivity.</p>

                    <h3>Environmental Impact</h3> 
                    <p>Organic farming practices, such as using natural pest control and composting, minimize soil degradation and chemical runoff, preserving ecosystems. Innovations like precision agriculture further optimize resource use, making farming truly eco-friendly.</p> 
                    <h3>Future Developments</h3> 
                    <p>Emerging innovations in organic farming include AI-driven soil monitoring, drone-assisted crop management, and renewable energy-powered farm operations. These advancements will enhance sustainability and efficiency in organic agriculture.</p>`
    },
};

function openModal(blogId) {
    const content = blogContent[blogId];
    modalTitle.textContent = content.title;
    modalText.innerHTML = content.content;
    modal.style.display = "block";
    document.body.style.overflow = "hidden";
}

function closeModal() {
    modal.style.display = "none";
    document.body.style.overflow = "auto";
}

window.onclick = function (event) {
    if (event.target === modal) {
        closeModal();
    }
};

