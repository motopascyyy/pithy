# PiThy
#### The techies blogging platform

This project is designed to be a blogging platform that you host yourself on your own Linux server (e.g Linode, Digital Ocean).

PiThy does not store ANY data. Every blogpost or image is sourced from a Git Repo that you provide (AKA Blog Repo). It provides an RSS feed so that people can follow your blog from the comfort of their preferred RSS reader.

Since the content is hosted on Git, PiThy will `fetch` for updates every minute or so. If new content on the current branch is detected, it will `pull` those updates to make them available. I personally have my blog repo at GitHub but other providers should work just fine as origin sources.

## Getting Started

1. Spin up a Linux VM.
2. Install Open JDK 17+ or whichever distribution of Java that you prefer. The only hard requirement is for it to be JDK 17+ compliant.
    - [Installing OpenJDK 17 on Ubuntu](https://www.linuxcapable.com/how-to-install-openjdk-17-on-ubuntu-20-04/)
3. Clone your repo containing your blog posts 
4. Clone this repo and build the project (instructions below). Anything on the branch `main` is considered stable.
5. Run the code following the instructions below

## Build the project
```shell
git clone https://github.com/motopascyyy/pithy.git
cd pithy
./gradlew clean build
```

## Running the application
Example:
```shell
cd pithy/build/libs
java -jar pithy-0.1-SNAPSHOT.jar --blog.url="http://yoururl.com" --git.repoPath="" --git.user="joe_dough" --git.password="your_secrEt-Key"
```

### Critical Command/Argument Description

The following arguments are critical for the service to start up. Without them, it will crash on launch (by design). There isn't a lot of validation though so please ensure the values are accurate.

### Main `java` command
- Key: `java -jar`
- Description: This is how you run a java process.  The piece after `java -jar pithy-0.1-SNAPSHOT.jar` is the name of the file that was built after you've compiled the project with the commands above. 
- Example: `java -jar pithy-0.1-SNAPSHOT.jar`

#### Blog URL
- Key: `--blog.url`
- Description: This is the root URL for your website. It's needed to populate the correct links in the RSS feed that will allow people to subscribe to your blog
- Example: `--blog.url="http://yoururl.com"`

#### Git Repo Path
- Key: `--git.repoPath`
- Description: This is the path where you cloned the repo that will contain all of your blog posts
- `"~/my-awesome-blog-repo/"`
- Example: `--git.repoPath="~/my-awesome-blog-repo/"`

### Git User Name
- Key: `--git.user=`
- Description: This is the username you use to fetch the content from the blog repo. While PiThy will never commit or push anything to your repo, I recommend you make sure that restrictions are in place to ensure no writing can occur.
- Example: `--git.user="joe_dough"`

### Git Password
- Key: `--git.password`
- Description: Password or security token used to authenticate against the origin repo. Similar warnings with the username, I recommend you put restrictions on your repo such that this user/pass combo can never write to your repo. 
- Example: `--git.password="your_secrEt-Key"`

There are other properties, but they aren't yet documented and are considered much less critical. Default values are provided and changing them isn't recommended.

## Blog Repo Structure
PiThy makes a few assumptions about where files are stored in your repo. Your repo should be structured as such

```shell
my-awesome-blog-repo/
|── fixed_pages
    |── about-me.md
    |── resume.md
|── post1.md
|── post2.md
|── post3.md
```

### Blog Posts
Blog posts should be in the root directory of the repo. Only `*.md` files will be found and looked at.

### Fixed Pages
The `fixed_pages` directory is a special directory which contains. All files here will appear as dedicated link in the navigation bar at the top of the screen. Similar to the regular blog posts, only `*.md` files will be listed. If you click on those files, it will also assume that the contents are Markdown files. The order of the links in the navigation bar is determined by filename, but can be overwritten by adding the following YAMLFrontMatter to your files:
```markdown
---
order:0
---
Hi everyone,

My name is Joe Dough....
```

### Media Assets
Things like images, video or audio are your own responsibility to provide. You can commit those to your Blog repo, upload to a CDN, or manually copy them to a web accessible place on your server. At this time, I don't provide much support for media assets. This will improve in the future but for now, please host your files elsewhere or use services such as Youtube/Vimeo which offer way better support for media hosting.

The `order` value will be taken into consideration and prioritized over the file name. If 2 files share the same order, then the filename based sorting will take effect. The same logic applies if a bad number value is provided in the order (e.g decimal numbers or non-numeric characters).

[YAML Front Matter Additional Information](https://github.com/commonmark/commonmark-java#yaml-front-matter)

## Future (in no particular order)

### Simpler deployments
I plan to publish a Docker Image that makes installation and setup a lot easier. For now, you'll need to clone and compile from source, though it's pretty quick and painless. This should eliminate the need to setup your own JDK and compile the project from scratch, though the option will always be available.

### Basic Analytics
I also plan to add some very basic analytics and a dashboard to track site traffic. This will be heavily anonymized and entirely driven by server-side data capturing. It's meant to be a privacy-friendly, but very basic alternative to things like Google Analytics.

### Admin Panel
This will replace the need for the command line arguments. Everything will be stored in a small file based DB. This will also allow starting the service without needing to clone the blog repo first.

### Custom Styling
There will be a way to provide your own custom CSS to the site to allow you to personalize the look and feel to your heart's content. There will also be a way to include your own logo file.

### Custom Header and Footer
There will be a way for you to provide your own custom header and footer html pages

### Better handling of media assets
I will provide a solution for uploading or referencing media assets such as images, videos or audio files.

### Drafts
I will create a facility to allow you to publish drafts without having them appear on the main feed. The current thought is that you would commit your drafts to a separate "drafts" branch. In a later state, there may also exist some form of authentication mechanism so that only specific people can see the posted content, but that's for a later time.

### Mail List Integration
Sometime down the line, I'll integrate with services like Mailchimp to allow you to have people sign-up for newsletters.

And much much more...

## Q & A
#### Why JDK 17 (or Java at all)?
I built this application in Java because it's what I'm most comfortable with. It's not the newest or flashiest, but it has a ton of support and some of the newest developments in the language are helping to modernize it. I personally build against JDK 17 (and recommend that) because it's the LTS version. It's missing out on some of the flashiest features, but stability and support is more important to me.

#### Can I run this on Windows/macOS?
Yes, you can. Since this is written entirely in Java, there are no known OS level dependencies. That being said, I develop on a Mac and deploy to Linux VMs. If you deviate from this known state, your mileage may vary. That, and most of the IaaS/PaaS offerings tend to prefer running Linux anyway, so why fight it?

#### Why do you only process markdown files?
Markdown was developed as a relatively simple syntax for publishing content to the web, without needing the full HTML syntax. It has become incredibly popular among tech publishers, developers and many others. There are a ton of apps which support syntax highlighting as well. The other beautiful thing is that it's entirely text based. This means that if you choose to move your content to another platform, there's no lock in to PiThy. No content hidden inside a database, or custom syntax that can't be processed anywhere else. While I may offer additional functionality in the form of YAMLFrontMatter to allow for some customization, none of this should impact the content published. 