/*
 
 Copyright 2015 HJOW

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 
 */

package hjow.hgtable.util;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import hjow.hgtable.Manager;
import hjow.hgtable.jscript.module.Module;
import hjow.hgtable.tableset.TableSetBuilder;
import hjow.hgtable.tableset.TableSetDownloader;
import hjow.hgtable.tableset.TableSetUploader;
import hjow.hgtable.tableset.TableSetWriter;
import hjow.hgtable.ui.swing.SwingManager;

/**
 * <p>이 클래스에는 라이센스 내용과 관련된 정적 메소드들이 있습니다.</p>
 * 
 * @author HJOW
 *
 */
public class LicenseUtil
{	
	public static final String titles = "Huge Table";
	public static final String copyrightYear  = "2015";
	public static final String copyrightOwner = "HJOW";
	public static final String copyrightEmail = "hujinone22@naver.com";
	
	/**
	 * <p>스칼라 라이센스 내용을 반환합니다.</p>
	 * 
	 * @return Scala License
	 */
	public static String scalaLicense()
	{
	      StringBuffer r = new StringBuffer("");
	      r = r.append("Copyright (c) 2002-2015 EPFL\n");
	      r = r.append("Copyright (c) 2011-2015 Typesafe, Inc.\n");
	      r = r.append(" \n");
	      r = r.append("All rights reserved.\n");
	      r = r.append(" \n");
	      r = r.append("Redistribution and use in source and binary forms, with or without modification, are permitted provided\n");
	      r = r.append(" that the following conditions are met:\n");
	      r = r.append(" \n");
	      r = r.append("Redistributions of source code must retain the above copyright notice, this list of conditions and the\n");
	      r = r.append(" following disclaimer.\n");
	      r = r.append("Redistributions in binary form must reproduce the above copyright notice, this list of conditions and\n");
	      r = r.append(" the following disclaimer in the documentation and/or other materials provided with the distribution.\n");
	      r = r.append(" \n");
	      r = r.append("Neither the name of the EPFL nor the names of its contributors may be used to endorse or promote \n");
	      r = r.append("products derived from this software without specific prior written permission.\n");
	      r = r.append("THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS “AS IS” AND ANY EXPRESS OR IMPLIED\n");
	      r = r.append(" WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A\n");
	      r = r.append(" PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR\n");
	      r = r.append(" ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT \n");
	      r = r.append("LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS \n");
	      r = r.append("INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR \n");
	      r = r.append("TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF \n");
	      r = r.append("ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.\n");
	      return r.toString();
	}
	
	/**
	 * <p>GNU Lesser General Public License (LGPL) 2.1 라이센스 내용을 반환합니다.</p>
	 * 
	 * @return LGPL license agreements
	 */
	public static String lgplLicense()
	{
		StringBuffer r = new StringBuffer("");
	      r = r.append("GNU LESSER GENERAL PUBLIC LICENSE\n");
	      r = r.append(" \n");
	      r = r.append("Version 2.1, February 1999\n");
	      r = r.append(" \n");
	      r = r.append("Copyright (C) 1991, 1999 Free Software Foundation, Inc.\n");
	      r = r.append("51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA\n");
	      r = r.append("Everyone is permitted to copy and distribute verbatim copies\n");
	      r = r.append("of this license document, but changing it is not allowed.\n");
	      r = r.append(" \n");
	      r = r.append("[This is the first released version of the Lesser GPL.  It also counts\n");
	      r = r.append(" as the successor of the GNU Library Public License, version 2, hence\n");
	      r = r.append(" the version number 2.1.]\n");
	      r = r.append("Preamble\n");
	      r = r.append(" \n");
	      r = r.append("The licenses for most software are designed to take away your freedom to share and change it. By contrast, the GNU General Public Licenses are intended to guarantee your freedom to share and change free software--to make sure the software is free for all its users.\n");
	      r = r.append(" \n");
	      r = r.append("This license, the Lesser General Public License, applies to some specially designated software packages--typically libraries--of the Free Software Foundation and other authors who decide to use it. You can use it too, but we suggest you first think carefully about whether this license or the ordinary General Public License is the better strategy to use in any particular case, based on the explanations below.\n");
	      r = r.append(" \n");
	      r = r.append("When we speak of free software, we are referring to freedom of use, not price. Our General Public Licenses are designed to make sure that you have the freedom to distribute copies of free software (and charge for this service if you wish); that you receive source code or can get it if you want it; that you can change the software and use pieces of it in new free programs; and that you are informed that you can do these things.\n");
	      r = r.append(" \n");
	      r = r.append("To protect your rights, we need to make restrictions that forbid distributors to deny you these rights or to ask you to surrender these rights. These restrictions translate to certain responsibilities for you if you distribute copies of the library or if you modify it.\n");
	      r = r.append(" \n");
	      r = r.append("For example, if you distribute copies of the library, whether gratis or for a fee, you must give the recipients all the rights that we gave you. You must make sure that they, too, receive or can get the source code. If you link other code with the library, you must provide complete object files to the recipients, so that they can relink them with the library after making changes to the library and recompiling it. And you must show them these terms so they know their rights.\n");
	      r = r.append(" \n");
	      r = r.append("We protect your rights with a two-step method: (1) we copyright the library, and (2) we offer you this license, which gives you legal permission to copy, distribute and/or modify the library.\n");
	      r = r.append(" \n");
	      r = r.append("To protect each distributor, we want to make it very clear that there is no warranty for the free library. Also, if the library is modified by someone else and passed on, the recipients should know that what they have is not the original version, so that the original author's reputation will not be affected by problems that might be introduced by others.\n");
	      r = r.append(" \n");
	      r = r.append("Finally, software patents pose a constant threat to the existence of any free program. We wish to make sure that a company cannot effectively restrict the users of a free program by obtaining a restrictive license from a patent holder. Therefore, we insist that any patent license obtained for a version of the library must be consistent with the full freedom of use specified in this license.\n");
	      r = r.append(" \n");
	      r = r.append("Most GNU software, including some libraries, is covered by the ordinary GNU General Public License. This license, the GNU Lesser General Public License, applies to certain designated libraries, and is quite different from the ordinary General Public License. We use this license for certain libraries in order to permit linking those libraries into non-free programs.\n");
	      r = r.append(" \n");
	      r = r.append("When a program is linked with a library, whether statically or using a shared library, the combination of the two is legally speaking a combined work, a derivative of the original library. The ordinary General Public License therefore permits such linking only if the entire combination fits its criteria of freedom. The Lesser General Public License permits more lax criteria for linking other code with the library.\n");
	      r = r.append(" \n");
	      r = r.append("We call this license the \"Lesser\" General Public License because it does Less to protect the user's freedom than the ordinary General Public License. It also provides other free software developers Less of an advantage over competing non-free programs. These disadvantages are the reason we use the ordinary General Public License for many libraries. However, the Lesser license provides advantages in certain special circumstances.\n");
	      r = r.append(" \n");
	      r = r.append("For example, on rare occasions, there may be a special need to encourage the widest possible use of a certain library, so that it becomes a de-facto standard. To achieve this, non-free programs must be allowed to use the library. A more frequent case is that a free library does the same job as widely used non-free libraries. In this case, there is little to gain by limiting the free library to free software only, so we use the Lesser General Public License.\n");
	      r = r.append(" \n");
	      r = r.append("In other cases, permission to use a particular library in non-free programs enables a greater number of people to use a large body of free software. For example, permission to use the GNU C Library in non-free programs enables many more people to use the whole GNU operating system, as well as its variant, the GNU/Linux operating system.\n");
	      r = r.append(" \n");
	      r = r.append("Although the Lesser General Public License is Less protective of the users' freedom, it does ensure that the user of a program that is linked with the Library has the freedom and the wherewithal to run that program using a modified version of the Library.\n");
	      r = r.append(" \n");
	      r = r.append("The precise terms and conditions for copying, distribution and modification follow. Pay close attention to the difference between a \"work based on the library\" and a \"work that uses the library\". The former contains code derived from the library, whereas the latter must be combined with the library in order to run.\n");
	      r = r.append(" \n");
	      r = r.append("TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION\n");
	      r = r.append(" \n");
	      r = r.append("0. This License Agreement applies to any software library or other program which contains a notice placed by the copyright holder or other authorized party saying it may be distributed under the terms of this Lesser General Public License (also called \"this License\"). Each licensee is addressed as \"you\".\n");
	      r = r.append(" \n");
	      r = r.append("A \"library\" means a collection of software functions and/or data prepared so as to be conveniently linked with application programs (which use some of those functions and data) to form executables.\n");
	      r = r.append(" \n");
	      r = r.append("The \"Library\", below, refers to any such software library or work which has been distributed under these terms. A \"work based on the Library\" means either the Library or any derivative work under copyright law: that is to say, a work containing the Library or a portion of it, either verbatim or with modifications and/or translated straightforwardly into another language. (Hereinafter, translation is included without limitation in the term \"modification\".)\n");
	      r = r.append(" \n");
	      r = r.append("\"Source code\" for a work means the preferred form of the work for making modifications to it. For a library, complete source code means all the source code for all modules it contains, plus any associated interface definition files, plus the scripts used to control compilation and installation of the library.\n");
	      r = r.append(" \n");
	      r = r.append("Activities other than copying, distribution and modification are not covered by this License; they are outside its scope. The act of running a program using the Library is not restricted, and output from such a program is covered only if its contents constitute a work based on the Library (independent of the use of the Library in a tool for writing it). Whether that is true depends on what the Library does and what the program that uses the Library does.\n");
	      r = r.append(" \n");
	      r = r.append("1. You may copy and distribute verbatim copies of the Library's complete source code as you receive it, in any medium, provided that you conspicuously and appropriately publish on each copy an appropriate copyright notice and disclaimer of warranty; keep intact all the notices that refer to this License and to the absence of any warranty; and distribute a copy of this License along with the Library.\n");
	      r = r.append(" \n");
	      r = r.append("You may charge a fee for the physical act of transferring a copy, and you may at your option offer warranty protection in exchange for a fee.\n");
	      r = r.append(" \n");
	      r = r.append("2. You may modify your copy or copies of the Library or any portion of it, thus forming a work based on the Library, and copy and distribute such modifications or work under the terms of Section 1 above, provided that you also meet all of these conditions:\n");
	      r = r.append(" \n");
	      r = r.append("a) The modified work must itself be a software library.\n");
	      r = r.append("b) You must cause the files modified to carry prominent notices stating that you changed the files and the date of any change.\n");
	      r = r.append("c) You must cause the whole of the work to be licensed at no charge to all third parties under the terms of this License.\n");
	      r = r.append("d) If a facility in the modified Library refers to a function or a table of data to be supplied by an application program that uses the facility, other than as an argument passed when the facility is invoked, then you must make a good faith effort to ensure that, in the event an application does not supply such function or table, the facility still operates, and performs whatever part of its purpose remains meaningful.\n");
	      r = r.append("(For example, a function in a library to compute square roots has a purpose that is entirely well-defined independent of the application. Therefore, Subsection 2d requires that any application-supplied function or table used by this function must be optional: if the application does not supply it, the square root function must still compute square roots.)\n");
	      r = r.append(" \n");
	      r = r.append("These requirements apply to the modified work as a whole. If identifiable sections of that work are not derived from the Library, and can be reasonably considered independent and separate works in themselves, then this License, and its terms, do not apply to those sections when you distribute them as separate works. But when you distribute the same sections as part of a whole which is a work based on the Library, the distribution of the whole must be on the terms of this License, whose permissions for other licensees extend to the entire whole, and thus to each and every part regardless of who wrote it.\n");
	      r = r.append(" \n");
	      r = r.append("Thus, it is not the intent of this section to claim rights or contest your rights to work written entirely by you; rather, the intent is to exercise the right to control the distribution of derivative or collective works based on the Library.\n");
	      r = r.append(" \n");
	      r = r.append("In addition, mere aggregation of another work not based on the Library with the Library (or with a work based on the Library) on a volume of a storage or distribution medium does not bring the other work under the scope of this License.\n");
	      r = r.append(" \n");
	      r = r.append("3. You may opt to apply the terms of the ordinary GNU General Public License instead of this License to a given copy of the Library. To do this, you must alter all the notices that refer to this License, so that they refer to the ordinary GNU General Public License, version 2, instead of to this License. (If a newer version than version 2 of the ordinary GNU General Public License has appeared, then you can specify that version instead if you wish.) Do not make any other change in these notices.\n");
	      r = r.append(" \n");
	      r = r.append("Once this change is made in a given copy, it is irreversible for that copy, so the ordinary GNU General Public License applies to all subsequent copies and derivative works made from that copy.\n");
	      r = r.append(" \n");
	      r = r.append("This option is useful when you wish to copy part of the code of the Library into a program that is not a library.\n");
	      r = r.append(" \n");
	      r = r.append("4. You may copy and distribute the Library (or a portion or derivative of it, under Section 2) in object code or executable form under the terms of Sections 1 and 2 above provided that you accompany it with the complete corresponding machine-readable source code, which must be distributed under the terms of Sections 1 and 2 above on a medium customarily used for software interchange.\n");
	      r = r.append(" \n");
	      r = r.append("If distribution of object code is made by offering access to copy from a designated place, then offering equivalent access to copy the source code from the same place satisfies the requirement to distribute the source code, even though third parties are not compelled to copy the source along with the object code.\n");
	      r = r.append(" \n");
	      r = r.append("5. A program that contains no derivative of any portion of the Library, but is designed to work with the Library by being compiled or linked with it, is called a \"work that uses the Library\". Such a work, in isolation, is not a derivative work of the Library, and therefore falls outside the scope of this License.\n");
	      r = r.append(" \n");
	      r = r.append("However, linking a \"work that uses the Library\" with the Library creates an executable that is a derivative of the Library (because it contains portions of the Library), rather than a \"work that uses the library\". The executable is therefore covered by this License. Section 6 states terms for distribution of such executables.\n");
	      r = r.append(" \n");
	      r = r.append("When a \"work that uses the Library\" uses material from a header file that is part of the Library, the object code for the work may be a derivative work of the Library even though the source code is not. Whether this is true is especially significant if the work can be linked without the Library, or if the work is itself a library. The threshold for this to be true is not precisely defined by law.\n");
	      r = r.append(" \n");
	      r = r.append("If such an object file uses only numerical parameters, data structure layouts and accessors, and small macros and small inline functions (ten lines or less in length), then the use of the object file is unrestricted, regardless of whether it is legally a derivative work. (Executables containing this object code plus portions of the Library will still fall under Section 6.)\n");
	      r = r.append(" \n");
	      r = r.append("Otherwise, if the work is a derivative of the Library, you may distribute the object code for the work under the terms of Section 6. Any executables containing that work also fall under Section 6, whether or not they are linked directly with the Library itself.\n");
	      r = r.append(" \n");
	      r = r.append("6. As an exception to the Sections above, you may also combine or link a \"work that uses the Library\" with the Library to produce a work containing portions of the Library, and distribute that work under terms of your choice, provided that the terms permit modification of the work for the customer's own use and reverse engineering for debugging such modifications.\n");
	      r = r.append(" \n");
	      r = r.append("You must give prominent notice with each copy of the work that the Library is used in it and that the Library and its use are covered by this License. You must supply a copy of this License. If the work during execution displays copyright notices, you must include the copyright notice for the Library among them, as well as a reference directing the user to the copy of this License. Also, you must do one of these things:\n");
	      r = r.append(" \n");
	      r = r.append("a) Accompany the work with the complete corresponding machine-readable source code for the Library including whatever changes were used in the work (which must be distributed under Sections 1 and 2 above); and, if the work is an executable linked with the Library, with the complete machine-readable \"work that uses the Library\", as object code and/or source code, so that the user can modify the Library and then relink to produce a modified executable containing the modified Library. (It is understood that the user who changes the contents of definitions files in the Library will not necessarily be able to recompile the application to use the modified definitions.)\n");
	      r = r.append("b) Use a suitable shared library mechanism for linking with the Library. A suitable mechanism is one that (1) uses at run time a copy of the library already present on the user's computer system, rather than copying library functions into the executable, and (2) will operate properly with a modified version of the library, if the user installs one, as long as the modified version is interface-compatible with the version that the work was made with.\n");
	      r = r.append("c) Accompany the work with a written offer, valid for at least three years, to give the same user the materials specified in Subsection 6a, above, for a charge no more than the cost of performing this distribution.\n");
	      r = r.append("d) If distribution of the work is made by offering access to copy from a designated place, offer equivalent access to copy the above specified materials from the same place.\n");
	      r = r.append("e) Verify that the user has already received a copy of these materials or that you have already sent this user a copy.\n");
	      r = r.append("For an executable, the required form of the \"work that uses the Library\" must include any data and utility programs needed for reproducing the executable from it. However, as a special exception, the materials to be distributed need not include anything that is normally distributed (in either source or binary form) with the major components (compiler, kernel, and so on) of the operating system on which the executable runs, unless that component itself accompanies the executable.\n");
	      r = r.append(" \n");
	      r = r.append("It may happen that this requirement contradicts the license restrictions of other proprietary libraries that do not normally accompany the operating system. Such a contradiction means you cannot use both them and the Library together in an executable that you distribute.\n");
	      r = r.append(" \n");
	      r = r.append("7. You may place library facilities that are a work based on the Library side-by-side in a single library together with other library facilities not covered by this License, and distribute such a combined library, provided that the separate distribution of the work based on the Library and of the other library facilities is otherwise permitted, and provided that you do these two things:\n");
	      r = r.append(" \n");
	      r = r.append("a) Accompany the combined library with a copy of the same work based on the Library, uncombined with any other library facilities. This must be distributed under the terms of the Sections above.\n");
	      r = r.append("b) Give prominent notice with the combined library of the fact that part of it is a work based on the Library, and explaining where to find the accompanying uncombined form of the same work.\n");
	      r = r.append("8. You may not copy, modify, sublicense, link with, or distribute the Library except as expressly provided under this License. Any attempt otherwise to copy, modify, sublicense, link with, or distribute the Library is void, and will automatically terminate your rights under this License. However, parties who have received copies, or rights, from you under this License will not have their licenses terminated so long as such parties remain in full compliance.\n");
	      r = r.append(" \n");
	      r = r.append("9. You are not required to accept this License, since you have not signed it. However, nothing else grants you permission to modify or distribute the Library or its derivative works. These actions are prohibited by law if you do not accept this License. Therefore, by modifying or distributing the Library (or any work based on the Library), you indicate your acceptance of this License to do so, and all its terms and conditions for copying, distributing or modifying the Library or works based on it.\n");
	      r = r.append(" \n");
	      r = r.append("10. Each time you redistribute the Library (or any work based on the Library), the recipient automatically receives a license from the original licensor to copy, distribute, link with or modify the Library subject to these terms and conditions. You may not impose any further restrictions on the recipients' exercise of the rights granted herein. You are not responsible for enforcing compliance by third parties with this License.\n");
	      r = r.append(" \n");
	      r = r.append("11. If, as a consequence of a court judgment or allegation of patent infringement or for any other reason (not limited to patent issues), conditions are imposed on you (whether by court order, agreement or otherwise) that contradict the conditions of this License, they do not excuse you from the conditions of this License. If you cannot distribute so as to satisfy simultaneously your obligations under this License and any other pertinent obligations, then as a consequence you may not distribute the Library at all. For example, if a patent license would not permit royalty-free redistribution of the Library by all those who receive copies directly or indirectly through you, then the only way you could satisfy both it and this License would be to refrain entirely from distribution of the Library.\n");
	      r = r.append(" \n");
	      r = r.append("If any portion of this section is held invalid or unenforceable under any particular circumstance, the balance of the section is intended to apply, and the section as a whole is intended to apply in other circumstances.\n");
	      r = r.append(" \n");
	      r = r.append("It is not the purpose of this section to induce you to infringe any patents or other property right claims or to contest validity of any such claims; this section has the sole purpose of protecting the integrity of the free software distribution system which is implemented by public license practices. Many people have made generous contributions to the wide range of software distributed through that system in reliance on consistent application of that system; it is up to the author/donor to decide if he or she is willing to distribute software through any other system and a licensee cannot impose that choice.\n");
	      r = r.append(" \n");
	      r = r.append("This section is intended to make thoroughly clear what is believed to be a consequence of the rest of this License.\n");
	      r = r.append(" \n");
	      r = r.append("12. If the distribution and/or use of the Library is restricted in certain countries either by patents or by copyrighted interfaces, the original copyright holder who places the Library under this License may add an explicit geographical distribution limitation excluding those countries, so that distribution is permitted only in or among countries not thus excluded. In such case, this License incorporates the limitation as if written in the body of this License.\n");
	      r = r.append(" \n");
	      r = r.append("13. The Free Software Foundation may publish revised and/or new versions of the Lesser General Public License from time to time. Such new versions will be similar in spirit to the present version, but may differ in detail to address new problems or concerns.\n");
	      r = r.append(" \n");
	      r = r.append("Each version is given a distinguishing version number. If the Library specifies a version number of this License which applies to it and \"any later version\", you have the option of following the terms and conditions either of that version or of any later version published by the Free Software Foundation. If the Library does not specify a license version number, you may choose any version ever published by the Free Software Foundation.\n");
	      r = r.append(" \n");
	      r = r.append("14. If you wish to incorporate parts of the Library into other free programs whose distribution conditions are incompatible with these, write to the author to ask for permission. For software which is copyrighted by the Free Software Foundation, write to the Free Software Foundation; we sometimes make exceptions for this. Our decision will be guided by the two goals of preserving the free status of all derivatives of our free software and of promoting the sharing and reuse of software generally.\n");
	      r = r.append(" \n");
	      r = r.append("NO WARRANTY\n");
	      r = r.append(" \n");
	      r = r.append("15. BECAUSE THE LIBRARY IS LICENSED FREE OF CHARGE, THERE IS NO WARRANTY FOR THE LIBRARY, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE LIBRARY \"AS IS\" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO THE QUALITY AND PERFORMANCE OF THE LIBRARY IS WITH YOU. SHOULD THE LIBRARY PROVE DEFECTIVE, YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.\n");
	      r = r.append(" \n");
	      r = r.append("16. IN NO EVENT UNLESS REQUIRED BY APPLICABLE LAW OR AGREED TO IN WRITING WILL ANY COPYRIGHT HOLDER, OR ANY OTHER PARTY WHO MAY MODIFY AND/OR REDISTRIBUTE THE LIBRARY AS PERMITTED ABOVE, BE LIABLE TO YOU FOR DAMAGES, INCLUDING ANY GENERAL, SPECIAL, INCIDENTAL OR CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OR INABILITY TO USE THE LIBRARY (INCLUDING BUT NOT LIMITED TO LOSS OF DATA OR DATA BEING RENDERED INACCURATE OR LOSSES SUSTAINED BY YOU OR THIRD PARTIES OR A FAILURE OF THE LIBRARY TO OPERATE WITH ANY OTHER SOFTWARE), EVEN IF SUCH HOLDER OR OTHER PARTY HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.\n");
	      r = r.append(" \n");
	      r = r.append("END OF TERMS AND CONDITIONS\n");
	      r = r.append(" \n");
	      r = r.append("How to Apply These Terms to Your New Libraries\n");
	      r = r.append(" \n");
	      r = r.append("If you develop a new library, and you want it to be of the greatest possible use to the public, we recommend making it free software that everyone can redistribute and change. You can do so by permitting redistribution under these terms (or, alternatively, under the terms of the ordinary General Public License).\n");
	      r = r.append(" \n");
	      r = r.append("To apply these terms, attach the following notices to the library. It is safest to attach them to the start of each source file to most effectively convey the exclusion of warranty; and each file should have at least the \"copyright\" line and a pointer to where the full notice is found.\n");
	      r = r.append(" \n");
	      r = r.append("one line to give the library's name and an idea of what it does.\n");
	      r = r.append("Copyright (C) year  name of author\n");
	      r = r.append(" \n");
	      r = r.append("This library is free software; you can redistribute it and/or\n");
	      r = r.append("modify it under the terms of the GNU Lesser General Public\n");
	      r = r.append("License as published by the Free Software Foundation; either\n");
	      r = r.append("version 2.1 of the License, or (at your option) any later version.\n");
	      r = r.append(" \n");
	      r = r.append("This library is distributed in the hope that it will be useful,\n");
	      r = r.append("but WITHOUT ANY WARRANTY; without even the implied warranty of\n");
	      r = r.append("MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU\n");
	      r = r.append("Lesser General Public License for more details.\n");
	      r = r.append(" \n");
	      r = r.append("You should have received a copy of the GNU Lesser General Public\n");
	      r = r.append("License along with this library; if not, write to the Free Software\n");
	      r = r.append("Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA\n");
	      r = r.append("Also add information on how to contact you by electronic and paper mail.\n");
	      r = r.append(" \n");
	      r = r.append("You should also get your employer (if you work as a programmer) or your school, if any, to sign a \"copyright disclaimer\" for the library, if necessary. Here is a sample; alter the names:\n");
	      r = r.append(" \n");
	      r = r.append("Yoyodyne, Inc., hereby disclaims all copyright interest in\n");
	      r = r.append("the library `Frob' (a library for tweaking knobs) written\n");
	      r = r.append("by James Random Hacker.\n");
	      r = r.append(" \n");
	      r = r.append("signature of Ty Coon, 1 April 1990\n");
	      r = r.append("Ty Coon, President of Vice\n");
	      return r.toString();
	}
	/**
	 * <p>아파치 소프트웨어 라이센스 1.1 내용을 반환합니다.</p>
	 * 
	 * @return Apache Software License 1.1 agreements
	 */
	public static String apacheLicense()
	{
		StringBuffer r = new StringBuffer("");
	      r = r.append("/*\n");
	      r = r.append(" * ====================================================================\n");
	      r = r.append(" * \n");
	      r = r.append(" * The Apache Software License, Version 1.1\n");
	      r = r.append(" *\n");
	      r = r.append(" * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights\n");
	      r = r.append(" * reserved.\n");
	      r = r.append(" *\n");
	      r = r.append(" * Redistribution and use in source and binary forms, with or without\n");
	      r = r.append(" * modification, are permitted provided that the following conditions\n");
	      r = r.append(" * are met:\n");
	      r = r.append(" *\n");
	      r = r.append(" * 1. Redistributions of source code must retain the above copyright\n");
	      r = r.append(" *    notice, this list of conditions and the following disclaimer. \n");
	      r = r.append(" *\n");
	      r = r.append(" * 2. Redistributions in binary form must reproduce the above copyright\n");
	      r = r.append(" *    notice, this list of conditions and the following disclaimer in\n");
	      r = r.append(" *    the documentation and/or other materials provided with the\n");
	      r = r.append(" *    distribution.\n");
	      r = r.append(" *\n");
	      r = r.append(" * 3. The end-user documentation included with the redistribution,\n");
	      r = r.append(" *    if any, must include the following acknowledgement:  \n");
	      r = r.append(" *       \"This product includes software developed by the \n");
	      r = r.append(" *        Apache Software Foundation (http://www.apache.org/).\"\n");
	      r = r.append(" *    Alternately, this acknowledgement may appear in the software itself,\n");
	      r = r.append(" *    if and wherever such third-party acknowledgements normally appear.\n");
	      r = r.append(" *\n");
	      r = r.append(" * 4. The names \"Apache\", \"The Jakarta Project\", \"Commons\", and \"Apache Software\n");
	      r = r.append(" *    Foundation\" must not be used to endorse or promote products derived\n");
	      r = r.append(" *    from this software without prior written permission. For written \n");
	      r = r.append(" *    permission, please contact apache@apache.org.\n");
	      r = r.append(" *\n");
	      r = r.append(" * 5. Products derived from this software may not be called \"Apache\",\n");
	      r = r.append(" *    \"Apache\" nor may \"Apache\" appear in their name without prior \n");
	      r = r.append(" *    written permission of the Apache Software Foundation.\n");
	      r = r.append(" *\n");
	      r = r.append(" * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED\n");
	      r = r.append(" * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES\n");
	      r = r.append(" * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE\n");
	      r = r.append(" * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR\n");
	      r = r.append(" * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,\n");
	      r = r.append(" * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT\n");
	      r = r.append(" * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF\n");
	      r = r.append(" * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND\n");
	      r = r.append(" * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,\n");
	      r = r.append(" * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT\n");
	      r = r.append(" * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF\n");
	      r = r.append(" * SUCH DAMAGE.\n");
	      r = r.append(" * ====================================================================\n");
	      r = r.append(" *\n");
	      r = r.append(" * This software consists of voluntary contributions made by many\n");
	      r = r.append(" * individuals on behalf of the Apache Software Foundation.  For more\n");
	      r = r.append(" * information on the Apache Software Foundation, please see\n");
	      r = r.append(" * <http://www.apache.org/>.\n");
	      r = r.append(" *\n");
	      r = r.append(" */\n");
	      return r.toString();
	}
	
	/**
	 * <p>아파치 소프트웨어 라이센스 2.0 내용을 반환합니다.</p>
	 * 
	 * @return Apache Software License 2.0 agreements
	 */
	public static String apacheLicense2()
	{
		StringBuffer r = new StringBuffer("");
	      r = r.append("Apache License\n");
	      r = r.append("                           Version 2.0, January 2004\n");
	      r = r.append("                        http://www.apache.org/licenses/\n");
	      r = r.append(" \n");
	      r = r.append("   TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION\n");
	      r = r.append(" \n");
	      r = r.append("   1. Definitions.\n");
	      r = r.append(" \n");
	      r = r.append("      \"License\" shall mean the terms and conditions for use, reproduction,\n");
	      r = r.append("      and distribution as defined by Sections 1 through 9 of this document.\n");
	      r = r.append(" \n");
	      r = r.append("      \"Licensor\" shall mean the copyright owner or entity authorized by\n");
	      r = r.append("      the copyright owner that is granting the License.\n");
	      r = r.append(" \n");
	      r = r.append("      \"Legal Entity\" shall mean the union of the acting entity and all\n");
	      r = r.append("      other entities that control, are controlled by, or are under common\n");
	      r = r.append("      control with that entity. For the purposes of this definition,\n");
	      r = r.append("      \"control\" means (i) the power, direct or indirect, to cause the\n");
	      r = r.append("      direction or management of such entity, whether by contract or\n");
	      r = r.append("      otherwise, or (ii) ownership of fifty percent (50%) or more of the\n");
	      r = r.append("      outstanding shares, or (iii) beneficial ownership of such entity.\n");
	      r = r.append(" \n");
	      r = r.append("      \"You\" (or \"Your\") shall mean an individual or Legal Entity\n");
	      r = r.append("      exercising permissions granted by this License.\n");
	      r = r.append(" \n");
	      r = r.append("      \"Source\" form shall mean the preferred form for making modifications,\n");
	      r = r.append("      including but not limited to software source code, documentation\n");
	      r = r.append("      source, and configuration files.\n");
	      r = r.append(" \n");
	      r = r.append("      \"Object\" form shall mean any form resulting from mechanical\n");
	      r = r.append("      transformation or translation of a Source form, including but\n");
	      r = r.append("      not limited to compiled object code, generated documentation,\n");
	      r = r.append("      and conversions to other media types.\n");
	      r = r.append(" \n");
	      r = r.append("      \"Work\" shall mean the work of authorship, whether in Source or\n");
	      r = r.append("      Object form, made available under the License, as indicated by a\n");
	      r = r.append("      copyright notice that is included in or attached to the work\n");
	      r = r.append("      (an example is provided in the Appendix below).\n");
	      r = r.append(" \n");
	      r = r.append("      \"Derivative Works\" shall mean any work, whether in Source or Object\n");
	      r = r.append("      form, that is based on (or derived from) the Work and for which the\n");
	      r = r.append("      editorial revisions, annotations, elaborations, or other modifications\n");
	      r = r.append("      represent, as a whole, an original work of authorship. For the purposes\n");
	      r = r.append("      of this License, Derivative Works shall not include works that remain\n");
	      r = r.append("      separable from, or merely link (or bind by name) to the interfaces of,\n");
	      r = r.append("      the Work and Derivative Works thereof.\n");
	      r = r.append(" \n");
	      r = r.append("      \"Contribution\" shall mean any work of authorship, including\n");
	      r = r.append("      the original version of the Work and any modifications or additions\n");
	      r = r.append("      to that Work or Derivative Works thereof, that is intentionally\n");
	      r = r.append("      submitted to Licensor for inclusion in the Work by the copyright owner\n");
	      r = r.append("      or by an individual or Legal Entity authorized to submit on behalf of\n");
	      r = r.append("      the copyright owner. For the purposes of this definition, \"submitted\"\n");
	      r = r.append("      means any form of electronic, verbal, or written communication sent\n");
	      r = r.append("      to the Licensor or its representatives, including but not limited to\n");
	      r = r.append("      communication on electronic mailing lists, source code control systems,\n");
	      r = r.append("      and issue tracking systems that are managed by, or on behalf of, the\n");
	      r = r.append("      Licensor for the purpose of discussing and improving the Work, but\n");
	      r = r.append("      excluding communication that is conspicuously marked or otherwise\n");
	      r = r.append("      designated in writing by the copyright owner as \"Not a Contribution.\"\n");
	      r = r.append(" \n");
	      r = r.append("      \"Contributor\" shall mean Licensor and any individual or Legal Entity\n");
	      r = r.append("      on behalf of whom a Contribution has been received by Licensor and\n");
	      r = r.append("      subsequently incorporated within the Work.\n");
	      r = r.append(" \n");
	      r = r.append("   2. Grant of Copyright License. Subject to the terms and conditions of\n");
	      r = r.append("      this License, each Contributor hereby grants to You a perpetual,\n");
	      r = r.append("      worldwide, non-exclusive, no-charge, royalty-free, irrevocable\n");
	      r = r.append("      copyright license to reproduce, prepare Derivative Works of,\n");
	      r = r.append("      publicly display, publicly perform, sublicense, and distribute the\n");
	      r = r.append("      Work and such Derivative Works in Source or Object form.\n");
	      r = r.append(" \n");
	      r = r.append("   3. Grant of Patent License. Subject to the terms and conditions of\n");
	      r = r.append("      this License, each Contributor hereby grants to You a perpetual,\n");
	      r = r.append("      worldwide, non-exclusive, no-charge, royalty-free, irrevocable\n");
	      r = r.append("      (except as stated in this section) patent license to make, have made,\n");
	      r = r.append("      use, offer to sell, sell, import, and otherwise transfer the Work,\n");
	      r = r.append("      where such license applies only to those patent claims licensable\n");
	      r = r.append("      by such Contributor that are necessarily infringed by their\n");
	      r = r.append("      Contribution(s) alone or by combination of their Contribution(s)\n");
	      r = r.append("      with the Work to which such Contribution(s) was submitted. If You\n");
	      r = r.append("      institute patent litigation against any entity (including a\n");
	      r = r.append("      cross-claim or counterclaim in a lawsuit) alleging that the Work\n");
	      r = r.append("      or a Contribution incorporated within the Work constitutes direct\n");
	      r = r.append("      or contributory patent infringement, then any patent licenses\n");
	      r = r.append("      granted to You under this License for that Work shall terminate\n");
	      r = r.append("      as of the date such litigation is filed.\n");
	      r = r.append(" \n");
	      r = r.append("   4. Redistribution. You may reproduce and distribute copies of the\n");
	      r = r.append("      Work or Derivative Works thereof in any medium, with or without\n");
	      r = r.append("      modifications, and in Source or Object form, provided that You\n");
	      r = r.append("      meet the following conditions:\n");
	      r = r.append(" \n");
	      r = r.append("      (a) You must give any other recipients of the Work or\n");
	      r = r.append("          Derivative Works a copy of this License; and\n");
	      r = r.append(" \n");
	      r = r.append("      (b) You must cause any modified files to carry prominent notices\n");
	      r = r.append("          stating that You changed the files; and\n");
	      r = r.append(" \n");
	      r = r.append("      (c) You must retain, in the Source form of any Derivative Works\n");
	      r = r.append("          that You distribute, all copyright, patent, trademark, and\n");
	      r = r.append("          attribution notices from the Source form of the Work,\n");
	      r = r.append("          excluding those notices that do not pertain to any part of\n");
	      r = r.append("          the Derivative Works; and\n");
	      r = r.append(" \n");
	      r = r.append("      (d) If the Work includes a \"NOTICE\" text file as part of its\n");
	      r = r.append("          distribution, then any Derivative Works that You distribute must\n");
	      r = r.append("          include a readable copy of the attribution notices contained\n");
	      r = r.append("          within such NOTICE file, excluding those notices that do not\n");
	      r = r.append("          pertain to any part of the Derivative Works, in at least one\n");
	      r = r.append("          of the following places: within a NOTICE text file distributed\n");
	      r = r.append("          as part of the Derivative Works; within the Source form or\n");
	      r = r.append("          documentation, if provided along with the Derivative Works; or,\n");
	      r = r.append("          within a display generated by the Derivative Works, if and\n");
	      r = r.append("          wherever such third-party notices normally appear. The contents\n");
	      r = r.append("          of the NOTICE file are for informational purposes only and\n");
	      r = r.append("          do not modify the License. You may add Your own attribution\n");
	      r = r.append("          notices within Derivative Works that You distribute, alongside\n");
	      r = r.append("          or as an addendum to the NOTICE text from the Work, provided\n");
	      r = r.append("          that such additional attribution notices cannot be construed\n");
	      r = r.append("          as modifying the License.\n");
	      r = r.append(" \n");
	      r = r.append("      You may add Your own copyright statement to Your modifications and\n");
	      r = r.append("      may provide additional or different license terms and conditions\n");
	      r = r.append("      for use, reproduction, or distribution of Your modifications, or\n");
	      r = r.append("      for any such Derivative Works as a whole, provided Your use,\n");
	      r = r.append("      reproduction, and distribution of the Work otherwise complies with\n");
	      r = r.append("      the conditions stated in this License.\n");
	      r = r.append(" \n");
	      r = r.append("   5. Submission of Contributions. Unless You explicitly state otherwise,\n");
	      r = r.append("      any Contribution intentionally submitted for inclusion in the Work\n");
	      r = r.append("      by You to the Licensor shall be under the terms and conditions of\n");
	      r = r.append("      this License, without any additional terms or conditions.\n");
	      r = r.append("      Notwithstanding the above, nothing herein shall supersede or modify\n");
	      r = r.append("      the terms of any separate license agreement you may have executed\n");
	      r = r.append("      with Licensor regarding such Contributions.\n");
	      r = r.append(" \n");
	      r = r.append("   6. Trademarks. This License does not grant permission to use the trade\n");
	      r = r.append("      names, trademarks, service marks, or product names of the Licensor,\n");
	      r = r.append("      except as required for reasonable and customary use in describing the\n");
	      r = r.append("      origin of the Work and reproducing the content of the NOTICE file.\n");
	      r = r.append(" \n");
	      r = r.append("   7. Disclaimer of Warranty. Unless required by applicable law or\n");
	      r = r.append("      agreed to in writing, Licensor provides the Work (and each\n");
	      r = r.append("      Contributor provides its Contributions) on an \"AS IS\" BASIS,\n");
	      r = r.append("      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or\n");
	      r = r.append("      implied, including, without limitation, any warranties or conditions\n");
	      r = r.append("      of TITLE, NON-INFRINGEMENT, MERCHANTABILITY, or FITNESS FOR A\n");
	      r = r.append("      PARTICULAR PURPOSE. You are solely responsible for determining the\n");
	      r = r.append("      appropriateness of using or redistributing the Work and assume any\n");
	      r = r.append("      risks associated with Your exercise of permissions under this License.\n");
	      r = r.append(" \n");
	      r = r.append("   8. Limitation of Liability. In no event and under no legal theory,\n");
	      r = r.append("      whether in tort (including negligence), contract, or otherwise,\n");
	      r = r.append("      unless required by applicable law (such as deliberate and grossly\n");
	      r = r.append("      negligent acts) or agreed to in writing, shall any Contributor be\n");
	      r = r.append("      liable to You for damages, including any direct, indirect, special,\n");
	      r = r.append("      incidental, or consequential damages of any character arising as a\n");
	      r = r.append("      result of this License or out of the use or inability to use the\n");
	      r = r.append("      Work (including but not limited to damages for loss of goodwill,\n");
	      r = r.append("      work stoppage, computer failure or malfunction, or any and all\n");
	      r = r.append("      other commercial damages or losses), even if such Contributor\n");
	      r = r.append("      has been advised of the possibility of such damages.\n");
	      r = r.append(" \n");
	      r = r.append("   9. Accepting Warranty or Additional Liability. While redistributing\n");
	      r = r.append("      the Work or Derivative Works thereof, You may choose to offer,\n");
	      r = r.append("      and charge a fee for, acceptance of support, warranty, indemnity,\n");
	      r = r.append("      or other liability obligations and/or rights consistent with this\n");
	      r = r.append("      License. However, in accepting such obligations, You may act only\n");
	      r = r.append("      on Your own behalf and on Your sole responsibility, not on behalf\n");
	      r = r.append("      of any other Contributor, and only if You agree to indemnify,\n");
	      r = r.append("      defend, and hold each Contributor harmless for any liability\n");
	      r = r.append("      incurred by, or claims asserted against, such Contributor by reason\n");
	      r = r.append("      of your accepting any such warranty or additional liability.\n");
	      r = r.append(" \n");
	      r = r.append("   END OF TERMS AND CONDITIONS\n");
	      r = r.append(" \n");
	      r = r.append("   APPENDIX: How to apply the Apache License to your work.\n");
	      r = r.append(" \n");
	      r = r.append("      To apply the Apache License to your work, attach the following\n");
	      r = r.append("      boilerplate notice, with the fields enclosed by brackets \"[]\"\n");
	      r = r.append("      replaced with your own identifying information. (Don't include\n");
	      r = r.append("      the brackets!)  The text should be enclosed in the appropriate\n");
	      r = r.append("      comment syntax for the file format. We also recommend that a\n");
	      r = r.append("      file or class name and description of purpose be included on the\n");
	      r = r.append("      same \"printed page\" as the copyright notice for easier\n");
	      r = r.append("      identification within third-party archives.\n");
	      r = r.append(" \n");
	      r = r.append("   Copyright [yyyy] [name of copyright owner]\n");
	      r = r.append(" \n");
	      r = r.append("   Licensed under the Apache License, Version 2.0 (the \"License\");\n");
	      r = r.append("   you may not use this file except in compliance with the License.\n");
	      r = r.append("   You may obtain a copy of the License at\n");
	      r = r.append(" \n");
	      r = r.append("       http://www.apache.org/licenses/LICENSE-2.0\n");
	      r = r.append(" \n");
	      r = r.append("   Unless required by applicable law or agreed to in writing, software\n");
	      r = r.append("   distributed under the License is distributed on an \"AS IS\" BASIS,\n");
	      r = r.append("   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n");
	      r = r.append("   See the License for the specific language governing permissions and\n");
	      r = r.append("   limitations under the License.\n");
	      return r.toString();
	}
	
	/**
	 * <p>MIT 라이센스 내용을 반환합니다.
	 * 
	 * @param year : 년도
	 * @param owner : 소유자
	 * @return MIT 라이센스 동의서 전문
	 */
	public static String mitLicense(String year, String owner)
	{
		StringBuffer r = new StringBuffer("");
	      r = r.append("Copyright (c) " + year + " " + owner + "\n");
	      r = r.append(" \n");
	      r = r.append("Permission is hereby granted, free of charge, to any person\n");
	      r = r.append("obtaining a copy of this software and associated documentation\n");
	      r = r.append("files (the \"Software\"), to deal in the Software without\n");
	      r = r.append("restriction, including without limitation the rights to use,\n");
	      r = r.append("copy, modify, merge, publish, distribute, sublicense, and/or sell\n");
	      r = r.append("copies of the Software, and to permit persons to whom the\n");
	      r = r.append("Software is furnished to do so, subject to the following\n");
	      r = r.append("conditions:\n");
	      r = r.append(" \n");
	      r = r.append("The above copyright notice and this permission notice shall be\n");
	      r = r.append("included in all copies or substantial portions of the Software.\n");
	      r = r.append(" \n");
	      r = r.append("THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND,\n");
	      r = r.append("EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES\n");
	      r = r.append("OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND\n");
	      r = r.append("NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT\n");
	      r = r.append("HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,\n");
	      r = r.append("WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING\n");
	      r = r.append("FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR\n");
	      r = r.append("OTHER DEALINGS IN THE SOFTWARE.\n");
	      return r.toString();
	}
	
	/**
	 * <p>BSD 라이센스 내용을 반환합니다.
	 * 
	 * @param year : 년도
	 * @param owner : 소유자
	 * @return BSD 라이센스 동의서 전문
	 */
	public static String bsdLicense(String year, String owner)
	{
		StringBuffer r = new StringBuffer("");
	      r = r.append("In the original BSD license, both occurrences of the phrase \"COPYRIGHT HOLDERS AND CONTRIBUTORS\" in the disclaimer read \"REGENTS AND CONTRIBUTORS\".\n");
	      r = r.append(" \n");
	      r = r.append("Here is the license template:\n");
	      r = r.append(" \n");
	      r = r.append("Copyright (c) " + year + ", " + owner + "\n");
	      r = r.append("All rights reserved.\n");
	      r = r.append(" \n");
	      r = r.append("Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:\n");
	      r = r.append(" \n");
	      r = r.append("1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.\n");
	      r = r.append(" \n");
	      r = r.append("2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.\n");
	      r = r.append(" \n");
	      r = r.append("THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS \"AS IS\" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.\n");
	      return r.toString();
	}
	
	/**
	 * <p>이 메소드는 Huge Table 의 라이센스 내용을 반환합니다.</p>
	 * 
	 * @return Huge Table license agreements
	 */
	public static String hugeTableTitleMessage()
	{
		StringBuffer results = new StringBuffer("");
		
		results = results.append(Manager.applyStringTable("If you press or click OK button in this dialog, it means you agree all of these licenses.") + "\n" + "\n");
		results = results.append(Manager.applyStringTable("In Huge Table, another libraries are included.") + "\n");
		results = results.append(Manager.applyStringTable("You should see these license agreements.") + "\n");
		results = results.append(Manager.applyStringTable("If you don't agree of these, you cannot use these figures on this program.") + "\n");
		results = results.append(Manager.applyStringTable("For example, if you cannot agree MariaDB Connector libraries license, you should not connect to MariaDB Database with this program.") + "\n\n");
		results = results.append(Manager.applyStringTable("Apache POI") + "\n");
		results = results.append(Manager.applyStringTable("Apache Common Codecs") + "\n");
		results = results.append(Manager.applyStringTable("Google GSON") + "\n");
		results = results.append(Manager.applyStringTable("Sciss SyntaxPane") + "\n");
		results = results.append(Manager.applyStringTable("Scala Libraries") + "\n");
		results = results.append(Manager.applyStringTable("Cubrid JDBC Driver") + "\n");
		results = results.append(Manager.applyStringTable("MariaDB Connector for JDBC") + "\n");
		results = results.append(Manager.applyStringTable("You can see these license agreements on another tab in this dialog.") + "\n" + "\n");
		results = results.append(Manager.applyStringTable("Huge Table following Apache License 2.0, same as Apache POI.") + "\n");
		results = results.append(Manager.applyStringTable("But, it doesn't means \'Huge Table is developed by Apache Software Foundation.\'") + "\n");
		results = results.append(Manager.applyStringTable("So, you cannot get supports about Huge Table from Apache Software Foundation."));
		
		return results.toString();
	}
	
	/**
	 * <p>아파치 라이센스를 따르는 소프트웨어가 명시하는 문서 내용을 반환합니다.</p>
	 * 
	 * @return 아파치 라이센스 문서
	 */
	public static String apacheLicenseNotices(String years, String owners)
	{
	      StringBuffer r = new StringBuffer("");
	      r = r.append("Copyright " + years + " " + owners + "\n");
	      r = r.append(" \n");
	      r = r.append("Licensed under the Apache License, Version 2.0 (the \"License\");\n");
	      r = r.append("you may not use this file except in compliance with the License.\n");
	      r = r.append("You may obtain a copy of the License at\n");
	      r = r.append(" \n");
	      r = r.append("    http://www.apache.org/licenses/LICENSE-2.0\n");
	      r = r.append(" \n");
	      r = r.append("Unless required by applicable law or agreed to in writing, software\n");
	      r = r.append("distributed under the License is distributed on an \"AS IS\" BASIS,\n");
	      r = r.append("WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n");
	      r = r.append("See the License for the specific language governing permissions and\n");
	      r = r.append("limitations under the License.\n");
	      return r.toString();
	}
	
	/**
	 * <p>GUI 모드에서, 이 프로그램에 대한 정보 대화 상자에 보일 부제목입니다. null 시 부제목을 사용하지 않습니다.</p>
	 * 
	 * @return 부제목
	 */
	public static String edition()
	{
		return null;
	}
	
	/**
	 * <p>매니저 객체 초기화 시 라이센스에 따라 추가 설정을 합니다.</p>
	 * 
	 * @param manager : 매니저 객체
	 */
	public static void additionalInitializingManager(Manager manager)
	{
		
	}
	
	/**
	 * <p>GUI 매니저 객체 초기화 시 라이센스에 따라 추가 설정을 합니다. 이 메소드는 GUI 컴포넌트들이 초기화된 이후 호출됩니다.</p>
	 * 
	 * @param manager : GUI 매니저 객체
	 */
	public static void additionalInitializingGUI(SwingManager manager)
	{
		
	}
	
	/**
	 * <p>별도의 라이센스에 따라야 하는 모듈의 클래스 풀네임 리스트를 반환합니다.</p>
	 * 
	 * @return 모듈의 클래스 풀네임 리스트
	 */
	public static List<String> defaultClassModuleClassNames()
	{
		List<String> results = new Vector<String>();
//		results.add("externalModules.SIMSManager");
//		results.add("externalModules.NCSDataInsert");
		return results;
	}
	
	/**
	 * <p>라이센스에 종속되는 모듈들을 반환합니다.</p>
	 * 
	 * @return 모듈 리스트
	 */
	public static List<Module> modules()
	{
		List<Module> modules = new Vector<Module>();
		return modules;
	}
	
	/**
	 * <p>라이센스에 종속되는 모듈들을 위한 스트링 테이블 데이터들을 반환합니다.</p>
	 * 
	 * @param locale : 로캘 (시스템 언어)
	 * @return 스트링 테이블 리스트
	 */
	public static Map<String, String> stringTable(String locale)
	{
		Map<String, String> strData = new Hashtable<String, String>();
		return strData;
	}
	
	/**
	 * <p>라이센스에 종속되는 테이블 셋 저장 도구들을 반환합니다.</p>
	 * 
	 * @return 테이블 셋 저장 도구
	 */
	public static List<TableSetWriter> writers()
	{
		List<TableSetWriter> writers = new Vector<TableSetWriter>();
		return writers;
	}
	
	/**
	 * <p>라이센스에 종속되는 테이블 셋 불러오기 도구들을 반환합니다.</p>
	 * 
	 * @return 테이블 셋 불러오기 도구
	 */
	public static List<TableSetBuilder> builders()
	{
		List<TableSetBuilder> writers = new Vector<TableSetBuilder>();
		return writers;
	}
	
	/**
	 * <p>라이센스에 종속되는 테이블 셋 다운로드 도구들을 반환합니다.</p>
	 * 
	 * @return 테이블 셋 다운로드 도구
	 */
	public static List<TableSetDownloader> downloader()
	{
		List<TableSetDownloader> downloader = new Vector<TableSetDownloader>();
		return downloader;
	}
	
	/**
	 * <p>라이센스에 종속되는 테이블 셋 업로드 도구들을 반환합니다.</p>
	 * 
	 * @return 테이블 셋 업로드 도구
	 */
	public static List<TableSetUploader> uploader()
	{
		List<TableSetUploader> uploader = new Vector<TableSetUploader>();
		return uploader;
	}
	
	/**
	 * <p>라이센스 키를 반환합니다.</p>
	 * 
	 * @return 라이센스 키 값
	 */
	public static String authorizeThis()
	{
		return "c81bfe7d750c565c7767a04da0a0f07892d83652d0f2b7a4b30288c5624bae162b97c6fc020cb2d88128a06fc4b0a8a333e8dcdf2597c6d62bf3324761f9fb5f";
	}
}
