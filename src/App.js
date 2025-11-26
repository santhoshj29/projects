import React, { useState, useEffect } from 'react';
import { Github, Linkedin, Mail, Phone, ExternalLink, Code, Server, Cloud, Database, Award, Briefcase, GraduationCap, ChevronDown } from 'lucide-react';

export default function Portfolio() {
  const [activeSection, setActiveSection] = useState('home');
  const [isVisible, setIsVisible] = useState(false);

  useEffect(() => {
    setIsVisible(true);
    const handleScroll = () => {
      const sections = ['home', 'about', 'skills', 'projects', 'experience', 'education'];
      const current = sections.find(section => {
        const element = document.getElementById(section);
        if (element) {
          const rect = element.getBoundingClientRect();
          return rect.top <= 100 && rect.bottom >= 100;
        }
        return false;
      });
      if (current) setActiveSection(current);
    };
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  const scrollToSection = (id) => {
    document.getElementById(id)?.scrollIntoView({ behavior: 'smooth' });
  };

  const skills = {
    languages: ['Java', 'Python', 'JavaScript', 'C'],
    backend: ['Spring Boot', 'Spring MVC', 'Spring Security', 'JPA/Hibernate', 'REST APIs', 'Microservices', 'MySQL', 'PostgreSQL'],
    frontend: ['React.js', 'Axios', 'Material-UI', 'HTML', 'CSS'],
    cloud: ['AWS EC2', 'AWS S3', 'AWS IAM', 'CloudWatch', 'Docker', 'Jenkins', 'GitHub Actions', 'CI/CD'],
    tools: ['Git', 'Jira', 'Postman', 'Maven', 'Redis', 'Kafka']
  };

  const projects = [
    {
      name: 'PartPay',
      description: 'Full Stack Payroll & Scheduling Platform',
      highlights: [
        'Developed RESTful APIs in Spring Boot with JWT-based authentication',
        'Integrated Spring Data JPA with PostgreSQL for reliable persistence',
        'Built React frontend with Axios, improving reliability by 35%',
        'Automated CI/CD via GitHub Actions, reducing release cycles from 30 min to 2 min',
        'Deployed on AWS EC2 with Nginx load balancing (99% uptime)',
        'Implemented daily backup strategy with 7-day retention'
      ],
      tech: ['Spring Boot', 'React', 'PostgreSQL', 'AWS', 'Docker', 'GitHub Actions']
    }
  ];

  const experience = [
    {
      title: 'Student Assistant',
      company: 'University of North Texas',
      period: 'Jan 2024 - Dec 2025',
      achievements: [
        'Engineered backend updates for POS and kiosk systems across 10+ dining touchpoints',
        'Added error detection and retry logic, increasing order-ticket success by 40%',
        'Collaborated with campus IT team in Agile sprints and performed code reviews'
      ]
    },
    {
      title: 'Software Engineer Intern',
      company: 'BorgWarner IPEC',
      location: 'Bengaluru, India',
      period: 'Jan 2023 - Nov 2023',
      achievements: [
        'Developed Spring Boot application for CAN message logging, increasing accuracy by 40%',
        'Automated HIL testing pipelines with Python, Jenkins, and CANoe, reducing validation time by 30%',
        'Built embedded C modules for EV inverter ISR routines, reducing signal latency by 20%',
        'Maintained ISO 26262 documentation via Polarion'
      ]
    }
  ];

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-slate-800 to-slate-900 text-white">
      {/* Navigation */}
      <nav className="fixed top-0 w-full bg-slate-900/80 backdrop-blur-md z-50 border-b border-slate-700">
        <div className="max-w-6xl mx-auto px-4 py-4 flex justify-between items-center">
          <span className="text-2xl font-bold bg-gradient-to-r from-blue-400 to-purple-500 bg-clip-text text-transparent">
            SJ
          </span>
          <div className="flex gap-6">
            {['Home', 'About', 'Skills', 'Projects', 'Experience', 'Contact'].map(item => (
              <button
                key={item}
                onClick={() => scrollToSection(item.toLowerCase())}
                className={`hover:text-blue-400 transition-colors ${
                  activeSection === item.toLowerCase() ? 'text-blue-400' : ''
                }`}
              >
                {item}
              </button>
            ))}
          </div>
        </div>
      </nav>

      {/* Hero Section */}
      <section id="home" className="min-h-screen flex items-center justify-center px-4 pt-20">
        <div className={`text-center transition-all duration-1000 ${isVisible ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-10'}`}>
          <h1 className="text-6xl md:text-7xl font-bold mb-6 bg-gradient-to-r from-blue-400 via-purple-500 to-pink-500 bg-clip-text text-transparent">
            Santhosh Jonnadhula
          </h1>
          <p className="text-2xl md:text-3xl text-slate-300 mb-4">
            Java Full Stack Engineer
          </p>
          <p className="text-xl text-blue-400 mb-8">
            3 Years of Proven Experience
          </p>
          <p className="text-lg text-slate-400 max-w-2xl mx-auto mb-12">
            Designing and deploying scalable web applications using Spring Boot, React, and AWS
          </p>
          <div className="flex gap-6 justify-center mb-12">
            <a href="https://github.com/santhoshj29" target="_blank" rel="noopener noreferrer" 
               className="p-3 bg-slate-800 hover:bg-slate-700 rounded-full transition-all hover:scale-110">
              <Github size={24} />
            </a>
            <a href="https://www.linkedin.com/in/santhosh-jonnadhula-1255241a6" target="_blank" rel="noopener noreferrer"
               className="p-3 bg-slate-800 hover:bg-slate-700 rounded-full transition-all hover:scale-110">
              <Linkedin size={24} />
            </a>
            <a href="mailto:santhoshjonnadula29@gmail.com"
               className="p-3 bg-slate-800 hover:bg-slate-700 rounded-full transition-all hover:scale-110">
              <Mail size={24} />
            </a>
            <a href="tel:+19453082269"
               className="p-3 bg-slate-800 hover:bg-slate-700 rounded-full transition-all hover:scale-110">
              <Phone size={24} />
            </a>
          </div>
          <button onClick={() => scrollToSection('about')} className="animate-bounce">
            <ChevronDown size={32} className="text-blue-400" />
          </button>
        </div>
      </section>

      {/* About Section */}
      <section id="about" className="min-h-screen flex items-center justify-center px-4 py-20">
        <div className="max-w-4xl">
          <h2 className="text-4xl font-bold mb-8 text-center">About Me</h2>
          <div className="bg-slate-800/50 backdrop-blur rounded-2xl p-8 border border-slate-700">
            <p className="text-lg text-slate-300 leading-relaxed mb-6">
              I'm a Java Full Stack Engineer with 3 years of proven experience in building scalable web applications using 
              modern technologies. My expertise spans across backend development with Spring Boot, frontend development with 
              React, and cloud deployment on AWS.
            </p>
            <p className="text-lg text-slate-300 leading-relaxed mb-6">
              I specialize in designing RESTful APIs, integrating databases, and automating deployments with CI/CD pipelines. 
              My work has consistently improved system performance and developer efficiency, with measurable results like 
              reducing release cycles by 93% and increasing system reliability by 35%.
            </p>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mt-8">
              <div className="text-center p-4 bg-slate-900/50 rounded-lg">
                <Award className="mx-auto mb-2 text-blue-400" size={32} />
                <p className="text-sm text-slate-400">AWS Certified</p>
                <p className="font-semibold">Solutions Architect</p>
              </div>
              <div className="text-center p-4 bg-slate-900/50 rounded-lg">
                <Code className="mx-auto mb-2 text-purple-400" size={32} />
                <p className="text-sm text-slate-400">Full Stack</p>
                <p className="font-semibold">Development</p>
              </div>
              <div className="text-center p-4 bg-slate-900/50 rounded-lg">
                <Cloud className="mx-auto mb-2 text-pink-400" size={32} />
                <p className="text-sm text-slate-400">Cloud</p>
                <p className="font-semibold">Architecture</p>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Skills Section */}
      <section id="skills" className="min-h-screen flex items-center justify-center px-4 py-20">
        <div className="max-w-6xl w-full">
          <h2 className="text-4xl font-bold mb-12 text-center">Technical Skills</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="bg-slate-800/50 backdrop-blur rounded-2xl p-6 border border-slate-700 hover:border-blue-500 transition-all">
              <div className="flex items-center gap-3 mb-4">
                <Code className="text-blue-400" size={24} />
                <h3 className="text-xl font-semibold">Languages</h3>
              </div>
              <div className="flex flex-wrap gap-2">
                {skills.languages.map(skill => (
                  <span key={skill} className="px-3 py-1 bg-blue-500/20 text-blue-300 rounded-full text-sm">
                    {skill}
                  </span>
                ))}
              </div>
            </div>

            <div className="bg-slate-800/50 backdrop-blur rounded-2xl p-6 border border-slate-700 hover:border-purple-500 transition-all">
              <div className="flex items-center gap-3 mb-4">
                <Server className="text-purple-400" size={24} />
                <h3 className="text-xl font-semibold">Backend</h3>
              </div>
              <div className="flex flex-wrap gap-2">
                {skills.backend.map(skill => (
                  <span key={skill} className="px-3 py-1 bg-purple-500/20 text-purple-300 rounded-full text-sm">
                    {skill}
                  </span>
                ))}
              </div>
            </div>

            <div className="bg-slate-800/50 backdrop-blur rounded-2xl p-6 border border-slate-700 hover:border-pink-500 transition-all">
              <div className="flex items-center gap-3 mb-4">
                <Code className="text-pink-400" size={24} />
                <h3 className="text-xl font-semibold">Frontend</h3>
              </div>
              <div className="flex flex-wrap gap-2">
                {skills.frontend.map(skill => (
                  <span key={skill} className="px-3 py-1 bg-pink-500/20 text-pink-300 rounded-full text-sm">
                    {skill}
                  </span>
                ))}
              </div>
            </div>

            <div className="bg-slate-800/50 backdrop-blur rounded-2xl p-6 border border-slate-700 hover:border-green-500 transition-all">
              <div className="flex items-center gap-3 mb-4">
                <Cloud className="text-green-400" size={24} />
                <h3 className="text-xl font-semibold">Cloud & DevOps</h3>
              </div>
              <div className="flex flex-wrap gap-2">
                {skills.cloud.map(skill => (
                  <span key={skill} className="px-3 py-1 bg-green-500/20 text-green-300 rounded-full text-sm">
                    {skill}
                  </span>
                ))}
              </div>
            </div>

            <div className="bg-slate-800/50 backdrop-blur rounded-2xl p-6 border border-slate-700 hover:border-orange-500 transition-all md:col-span-2">
              <div className="flex items-center gap-3 mb-4">
                <Database className="text-orange-400" size={24} />
                <h3 className="text-xl font-semibold">Tools & Technologies</h3>
              </div>
              <div className="flex flex-wrap gap-2">
                {skills.tools.map(skill => (
                  <span key={skill} className="px-3 py-1 bg-orange-500/20 text-orange-300 rounded-full text-sm">
                    {skill}
                  </span>
                ))}
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Projects Section */}
      <section id="projects" className="min-h-screen flex items-center justify-center px-4 py-20">
        <div className="max-w-6xl w-full">
          <h2 className="text-4xl font-bold mb-12 text-center">Featured Project</h2>
          {projects.map(project => (
            <div key={project.name} className="bg-slate-800/50 backdrop-blur rounded-2xl p-8 border border-slate-700 hover:border-blue-500 transition-all">
              <div className="flex justify-between items-start mb-6">
                <div>
                  <h3 className="text-3xl font-bold mb-2">{project.name}</h3>
                  <p className="text-xl text-slate-400">{project.description}</p>
                </div>
                <ExternalLink className="text-blue-400" size={24} />
              </div>
              <div className="space-y-3 mb-6">
                {project.highlights.map((highlight, idx) => (
                  <div key={idx} className="flex items-start gap-3">
                    <div className="w-2 h-2 bg-blue-400 rounded-full mt-2 flex-shrink-0"></div>
                    <p className="text-slate-300">{highlight}</p>
                  </div>
                ))}
              </div>
              <div className="flex flex-wrap gap-2">
                {project.tech.map(tech => (
                  <span key={tech} className="px-3 py-1 bg-blue-500/20 text-blue-300 rounded-full text-sm">
                    {tech}
                  </span>
                ))}
              </div>
            </div>
          ))}
        </div>
      </section>

      {/* Experience Section */}
      <section id="experience" className="min-h-screen flex items-center justify-center px-4 py-20">
        <div className="max-w-6xl w-full">
          <h2 className="text-4xl font-bold mb-12 text-center">Work Experience</h2>
          <div className="space-y-6">
            {experience.map((exp, idx) => (
              <div key={idx} className="bg-slate-800/50 backdrop-blur rounded-2xl p-8 border border-slate-700 hover:border-purple-500 transition-all">
                <div className="flex justify-between items-start mb-4">
                  <div>
                    <h3 className="text-2xl font-bold">{exp.title}</h3>
                    <p className="text-lg text-purple-400">{exp.company}</p>
                    {exp.location && <p className="text-sm text-slate-400">{exp.location}</p>}
                  </div>
                  <div className="flex items-center gap-2 text-slate-400">
                    <Briefcase size={20} />
                    <span>{exp.period}</span>
                  </div>
                </div>
                <div className="space-y-2">
                  {exp.achievements.map((achievement, i) => (
                    <div key={i} className="flex items-start gap-3">
                      <div className="w-2 h-2 bg-purple-400 rounded-full mt-2 flex-shrink-0"></div>
                      <p className="text-slate-300">{achievement}</p>
                    </div>
                  ))}
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Education Section */}
      <section id="education" className="min-h-screen flex items-center justify-center px-4 py-20">
        <div className="max-w-6xl w-full">
          <h2 className="text-4xl font-bold mb-12 text-center">Education & Certifications</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
            <div className="bg-slate-800/50 backdrop-blur rounded-2xl p-8 border border-slate-700">
              <GraduationCap className="text-blue-400 mb-4" size={32} />
              <h3 className="text-xl font-bold mb-2">M.S. in Computer Science</h3>
              <p className="text-purple-400 mb-2">University of North Texas</p>
              <p className="text-slate-400">Dec 2025</p>
            </div>
            <div className="bg-slate-800/50 backdrop-blur rounded-2xl p-8 border border-slate-700">
              <GraduationCap className="text-pink-400 mb-4" size={32} />
              <h3 className="text-xl font-bold mb-2">B.Tech in ECE</h3>
              <p className="text-purple-400 mb-2">VIT Vellore</p>
              <p className="text-slate-400">2019-2023</p>
            </div>
          </div>
          <div className="bg-slate-800/50 backdrop-blur rounded-2xl p-8 border border-slate-700">
            <h3 className="text-2xl font-bold mb-6">Certifications</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="flex items-center gap-3 p-4 bg-slate-900/50 rounded-lg">
                <Award className="text-green-400" size={24} />
                <span>AWS Certified Cloud Practitioner</span>
              </div>
              <div className="flex items-center gap-3 p-4 bg-slate-900/50 rounded-lg">
                <Award className="text-green-400" size={24} />
                <span>AWS Certified Solutions Architect – Associate</span>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Contact Section */}
      <section id="contact" className="min-h-screen flex items-center justify-center px-4 py-20">
        <div className="max-w-4xl w-full text-center">
          <h2 className="text-4xl font-bold mb-8">Let's Connect</h2>
          <p className="text-xl text-slate-300 mb-12">
            I'm always open to discussing new opportunities, collaborations, or just talking tech!
          </p>
          <div className="flex flex-col md:flex-row gap-6 justify-center">
            <a href="mailto:santhoshjonnadula29@gmail.com" 
               className="flex items-center gap-3 px-8 py-4 bg-blue-600 hover:bg-blue-700 rounded-lg transition-all hover:scale-105">
              <Mail size={24} />
              <span>Email Me</span>
            </a>
            <a href="https://www.linkedin.com/in/santhosh-jonnadhula-1255241a6" target="_blank" rel="noopener noreferrer"
               className="flex items-center gap-3 px-8 py-4 bg-purple-600 hover:bg-purple-700 rounded-lg transition-all hover:scale-105">
              <Linkedin size={24} />
              <span>LinkedIn</span>
            </a>
            <a href="https://github.com/santhoshj29" target="_blank" rel="noopener noreferrer"
               className="flex items-center gap-3 px-8 py-4 bg-slate-700 hover:bg-slate-600 rounded-lg transition-all hover:scale-105">
              <Github size={24} />
              <span>GitHub</span>
            </a>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="bg-slate-900/80 border-t border-slate-700 py-8 text-center text-slate-400">
        <p>© 2025 Santhosh Jonnadhula. Built with React & Tailwind CSS.</p>
      </footer>
    </div>
  );
}