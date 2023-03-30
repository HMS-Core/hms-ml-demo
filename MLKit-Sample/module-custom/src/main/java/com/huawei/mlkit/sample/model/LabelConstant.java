/**
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.huawei.mlkit.sample.model;

public class LabelConstant {
    public static float[] THRES_TABLE = new float[] {0.13f, 0.07f, 0.5f, 0.12f, 0.08f, 0.5f, 0.05f, 0.22f, 0.11f, 0.15f,
        0.03f, 0.46f, 0.03f, 0.15f, 0.02f, 0.6f, 0.33f, 0.28f, 0.23f, 0.21f, 0.03f, 0.37f, 0.62f, 0.49f, 0.13f, 0.05f,
        0.15f, 0.03f, 0.09f, 0.32f, 0.01f, 0.58f, 0.07f, 0.5f, 0.34f, 0.09f, 0.31f, 0.07f, 0.06f, 0.48f, 0.2f, 0.5f,
        0.25f, 0.25f, 0.07f, 0.01f, 0.83f, 0.03f, 0.06f, 0.01f, 0.08f, 0.99f, 0.03f, 0.42f, 0.17f, 0.17f, 0.16f, 0.02f,
        0.1f, 0.05f, 0.54f, 0.38f, 0.24f, 0.44f, 0.13f, 0.06f, 0.19f, 0.5f, 0.59f, 0.5f, 0.07f, 0.19f, 0.14f, 0.07f,
        0.17f, 0.02f, 0.08f, 0.04f, 0.31f, 0.51f, 0.25f, 0.32f, 0.26f, 0.93f, 0.09f, 0.62f, 0.3f, 0.07f, 0.5f, 0.12f,
        0.12f, 0.5f, 0.18f, 0.43f, 0.21f, 0.4f, 0.08f, 0.18f, 0.5f, 0.35f, 0.99f, 0.33f, 0.22f, 0.13f, 0.04f, 0.08f,
        0.28f, 0.5f, 0.1f, 0.4f, 0.15f, 0.41f, 0.41f, 0.05f, 0.69f, 0.12f, 0.05f, 0.03f, 0.08f, 0.29f, 0.59f, 0.31f,
        0.05f, 0.02f, 0.04f, 0.79f, 0.02f, 0.08f, 0.01f, 0.06f, 0.21f, 0.04f, 0.1f, 0.08f, 0.24f, 0.16f, 0.09f, 0.05f,
        0.16f, 0.32f, 0.24f, 0.18f, 0.74f, 0.67f, 0.21f, 0.15f, 0.01f, 0.04f, 0.12f, 0.05f, 0.72f, 0.68f, 0.07f, 0.5f,
        0.08f, 0.02f, 0.26f, 0.24f, 0.74f, 0.85f, 0.12f, 0.04f, 0.24f, 0.12f, 0.1f, 0.31f, 0.09f, 0.85f, 0.56f, 0.66f,
        0.07f, 0.32f, 0.06f, 0.03f, 0.06f, 0.77f, 0.15f, 0.12f, 0.09f, 0.22f, 0.55f, 0.23f, 0.24f, 0.04f, 0.5f, 0.1f,
        0.07f, 0.05f, 0.11f, 0.5f, 0.08f, 0.06f, 0.65f, 0.16f, 0.12f, 0.06f, 0.35f, 0.09f, 0.13f, 0.27f, 0.08f, 0.12f,
        0.03f, 0.06f, 0.06f, 0.03f, 0.08f, 0.05f, 0.17f, 0.38f, 0.05f, 0.04f, 0.06f, 0.04f, 0.08f, 0.36f, 0.35f, 0.23f,
        0.04f, 0.21f, 0.31f, 0.74f, 0.27f, 0.14f, 0.1f, 0.1f, 0.05f, 0.51f, 0.01f, 0.51f, 0.1f, 0.68f, 0.04f, 0.5f,
        0.21f, 0.64f, 0.77f, 0.19f, 0.5f, 0.79f, 0.08f, 0.06f, 0.06f, 0.06f, 0.31f, 0.08f, 0.5f, 0.47f, 0.12f, 0.51f,
        0.79f, 0.04f, 0.63f, 0.15f, 0.07f, 0.5f, 0.5f, 0.14f, 0.37f, 0.1f, 0.5f, 0.48f, 0.15f, 0.47f, 0.09f, 0.07f,
        0.1f, 0.5f, 0.08f, 0.02f, 0.04f, 0.04f, 0.49f, 0.07f, 0.18f, 0.11f, 0.48f, 0.11f, 0.26f, 0.36f, 0.36f, 0.17f,
        0.19f, 0.2f, 0.17f, 0.5f, 0.03f, 0.11f, 0.05f, 0.08f, 0.17f, 0.11f, 0.34f, 0.91f, 0.27f, 0.2f, 0.04f, 0.42f,
        0.65f, 0.08f, 0.18f, 0.35f, 0.13f, 0.17f, 0.41f, 0.14f, 0.6f, 0.4f, 0.34f, 0.06f, 0.31f, 0.15f, 0.97f, 0.13f,
        0.01f, 0.2f, 0.09f, 0.86f, 0.05f, 0.09f, 0.24f, 0.08f, 0.29f, 0.16f, 0.04f, 0.5f, 0.42f, 0.11f, 0.01f, 0.01f,
        0.24f, 0.09f, 0.05f, 0.01f, 0.07f, 0.49f, 0.35f, 0.07f, 0.48f, 0.06f, 0.5f, 0.09f, 0.82f, 0.64f, 0.03f, 0.17f,
        0.5f, 0.47f, 0.4f, 0.6f, 0.09f, 0.02f, 0.01f, 0.03f, 0.5f, 0.07f, 0.08f, 0.03f, 0.03f, 0.01f, 0.28f, 0.42f,
        0.5f, 0.26f, 0.44f, 0.19f, 0.03f, 0.28f, 0.05f, 0.15f, 0.5f, 0.03f, 0.22f, 0.06f, 0.17f, 0.19f, 0.11f, 0.07f,
        0.3f, 0.34f, 0.12f, 0.01f, 0.06f, 0.13f, 0.5f, 0.24f, 0.9f, 0.27f, 0.32f, 0.36f, 0.86f, 0.25f, 0.15f, 0.34f,
        0.43f, 0.06f, 0.02f, 0.03f, 0.5f, 0.07f, 0.29f, 0.47f, 0.07f, 0.35f, 0.25f, 0.4f, 0.45f, 0.09f, 0.53f, 0.11f};

    public static String[] LABEL_TABLE = new String[] {"Herd", "Safari", "Bangle", "Cushion", "Countertop", "Prom",
        "Branch", "Sports", "Sky", "Community", "Wheel", "Cola", "Tuxedo", "Flowerpot", "Team", "Computer", "Unicycle",
        "Brig", "Aerospace engineering", "Scuba diving", "Goggles", "Fruit", "Badminton", "Horse", "Sunglasses", "Fun",
        "Prairie", "Poster", "Flag", "Speedboat", "Eyelash", "Veil", "Mobile phone", "Wheelbarrow", "Saucer", "Leather",
        "Drawer", "Paper", "Pier", "Waterfowl", "Tights", "Rickshaw", "Vegetable", "Handrail", "Ice", "Metal", "Flower",
        "Wing", "Silverware", "Event", "Skyline", "Money", "Comics", "Handbag", "Porcelain", "Rodeo", "Curtain", "Tile",
        "Human mouth", "Army", "Menu", "Boat", "Snowboarding", "Cairn terrier", "Net", "Pasteles", "Cup", "Rugby",
        "Pho", "Cap", "Human hair", "Surfing", "Loveseat", "Museum", "Shipwreck", "Trunk (Tree)", "Plush", "Monochrome",
        "Volcano", "Rock", "Pillow", "Presentation", "Nebula", "Subwoofer", "Lake", "Sledding", "Bangs", "Tablecloth",
        "Necklace", "Swimwear", "Standing", "Jeans", "Carnival", "Softball", "Centrepiece", "Skateboarder", "Cake",
        "Dragon", "Aurora", "Skiing", "Bathroom", "Dog", "Needlework", "Umbrella", "Church", "Fire", "Piano", "Denim",
        "Bridle", "Cabinetry", "Lipstick", "Ring", "Television", "Roller", "Seal", "Concert", "Product", "News",
        "Fast food", "Horn (Animal)", "Tattoo", "Bird", "Bridegroom", "Love", "Helmet", "Dinosaur", "Icing",
        "Miniature", "Tire", "Toy", "Icicle", "Jacket", "Coffee", "Mosque", "Rowing", "Wetsuit", "Camping",
        "Underwater", "Christmas", "Gelato", "Whiteboard", "Field", "Ragdoll", "Construction", "Lampshade", "Palace",
        "Meal", "Factory", "Cage", "Clipper (Boat)", "Gymnastics", "Turtle", "Human foot", "Marriage", "Web page",
        "Human beard", "Fog", "Wool", "Cappuccino", "Lighthouse", "Lego", "Sparkler", "Sari", "Model", "Temple",
        "Beanie", "Building", "Waterfall", "Penguin", "Cave", "Stadium", "Smile", "Human hand", "Park", "Desk",
        "Shetland sheepdog", "Bar", "Eating", "Neon", "Dalmatian", "Crocodile", "Wakeboarding", "Longboard", "Road",
        "Race", "Kitchen", "Odometer", "Cliff", "Fiction", "School", "Interaction", "Bullfighting", "Boxer", "Gown",
        "Aquarium", "Superhero", "Pie", "Asphalt", "Surfboard", "Cheeseburger", "Screenshot", "Supper", "Laugh",
        "Lunch", "Party", "Glacier", "Bench", "Grandparent", "Sink", "Pomacentridae", "Blazer", "Brick", "Space",
        "Backpacking", "Stuffed toy", "Sushi", "Glitter", "Bonfire", "Castle", "Marathon", "Pizza", "Beach",
        "Human ear", "Racing", "Sitting", "Iceberg", "Shelf", "Vehicle", "Pop music", "Playground", "Clown", "Car",
        "Rein", "Fur", "Musician", "Casino", "Baby", "Alcohol", "Strap", "Reef", "Balloon", "Outerwear", "Cathedral",
        "Competition", "Joker", "Blackboard", "Bunk bed", "Bear", "Moon", "Archery", "Polo", "River", "Fishing",
        "Ferris wheel", "Mortarboard", "Bracelet", "Flesh", "Statue", "Farm", "Desert", "Chain", "Aircraft", "Textile",
        "Hot dog", "Knitting", "Singer", "Juice", "Circus", "Chair", "Musical instrument", "Room", "Crochet",
        "Sailboat", "Newspaper", "Santa claus", "Swamp", "Skyscraper", "Skin", "Rocket", "Aviation", "Airliner",
        "Garden", "Ruins", "Storm", "Glasses", "Balance", "Nail (Body part)", "Rainbow", "Soil", "Vacation",
        "Moustache", "Doily", "Food", "Bride", "Cattle", "Pocket", "Infrastructure", "Train", "Gerbil", "Fireworks",
        "Pet", "Dam", "Crew", "Couch", "Bathing", "Quilting", "Motorcycle", "Butterfly", "Sled", "Watercolor paint",
        "Rafting", "Monument", "Lightning", "Sunset", "Bumper", "Shoe", "Waterskiing", "Sneakers", "Tower", "Insect",
        "Pool", "Placemat", "Airplane", "Plant", "Jungle", "Armrest", "Duck", "Dress", "Tableware", "Petal", "Bus",
        "Hanukkah", "Forest", "Hat", "Barn", "Tubing", "Snorkeling", "Cool", "Cookware and bakeware", "Cycling",
        "Swing (Seat)", "Muscle", "Cat", "Skateboard", "Star", "Toe", "Junk", "Bicycle", "Bedroom", "Person", "Sand",
        "Canyon", "Tie", "Twig", "Sphynx", "Supervillain", "Nightclub", "Ranch", "Pattern", "Shorts", "Himalayan",
        "Wall", "Leggings", "Windsurfing", "Deejay", "Dance", "Van", "Bento", "Sleep", "Wine", "Picnic", "Leisure",
        "Dune", "Crowd", "Kayak", "Ballroom", "Selfie", "Graduation", "Frigate", "Mountain", "Dude", "Windshield",
        "Skiff", "Class", "Scarf", "Bull", "Soccer", "Bag", "Basset hound", "Tractor", "Swimming", "Running", "Track",
        "Helicopter", "Pitch", "Clock", "Song", "Jersey", "Stairs", "Flap", "Jewellery", "Bridge", "Cuisine", "Bread",
        "Caving", "Shell", "Wreath", "Roof", "Cookie", "Canoe", "Apartment layout diagram", "ID Card",
        "Air conditioning", "Skirt", "Group photo", "The Oriental Pearl", "Guangzhou Tower", "Eiffel Tower",
        "Statue of Liberty", "Business card", "Draw-bar box", "Manicure", "Guitar", "Bracelet", "Muppet", "Screenshot",
        "Fullppt", "Bar code", "QR code", "Short sleeved shirt", "Bracelet Necklace", "Backpack", "Drinks", "Trousers",
        "Long sleeved blouse", "Underwear", "Flower without background", "Green plant", "Green plant side",
        "Building close view", "Tower", "Bridge", "Green plant distant view", "Emulsion cosmetics", "Camera lens",
        "Computer mouse", "Computer keyboard", "Sign", "Traditional Chinese painting", "Oil Painting", "Bamboo forest",
        "Cat and dog cage", "Supermarket goods", "Shop sign", "Moire face", "Dog negative sample child", "Canopy",
        "Spike", "Cryptodont", "Raceme", "Fruit", "Food", "Barbecue", "Bread", "Nut", "Seafood", "Capitulum", "Car",
        "Landmark Building", "Soups", "Western style food", "Hamburger", "Pancake", "Noodle & Pastries", "Toy",
        "Document", "Smartphone", "Porcelain", "Washing machine", "Shorts", "Laptop", "PCB board", "Ring", "Mask",
        "Chair", "Crocodile", "Cat", "Coin", "Couch", "Door", "Hat", "Bank card", "Wardrobe", "Food", "Woods", "Bed",
        "Baby carriage", "Refrigerator", "Table", "Piano", "Passport", "Penguin", "Lipstick", "Rabbit", "Shoe", "Sushi",
        "Television", "Handbag", "Face cream", "Dog", "Toy block", "Flower", "Building", "Camera", "Watch",
        "Wedding dress", "Baby", "Glasses", "Car", "Alcohol", "Perfume", "Selfie", "Animal", "Painting"};
}
